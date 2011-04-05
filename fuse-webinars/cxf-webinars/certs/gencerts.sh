
# This file also serves as a specification on what needs to be done to
# get the underlying CXF to work correctly.
# For the most part, you need to use only JKS (Java Key Store) formatted
# keystores and truststores.

# Initialize the default openssl DataBase.
# According to a default /usr/lib/ssl/openssl.cnf file it is ./demoCA
# Depending on the Openssl version, comment out "crlnumber" in config file.
# We echo 1345 to start the certificate serial number counter.

    rm -rf demoCA
    mkdir -p demoCA/newcerts
    cp /dev/null demoCA/index.txt
    echo "1345" > demoCA/serial

# This file makes sure that the certificate for CN=TheRA can be a Certificate
# Authority, i.e. can sign the user certificates, e.g. "CN=fuse-esb".

cat <<EOF > exts
[x509_extensions]
basicConstraints=CA:TRUE
EOF

# Create the CA's keypair and self-signed certificate
#   -x509 means create self-sign cert
#   -keyout means generate keypair
#   -nodes means do not encrypt private key.
#   -set_serial sets the serial number of the certificate

    openssl req -verbose -x509 -new -nodes -set_serial 1234 \
    -subj "/CN=CA/OU=CXF Webinar/O=fusesource.com/ST=DU/C=IE" \
    -days 7300 -out cacert.pem -keyout caprivkey.pem 

# Create keypairs and Cert Request for a certificate for CN=fuse-esb and CN=scott
# This procedure must be done in JKS, because we need to use a JKS keystore.
# The current version of CXF using PCKS12 will not work for a number of 
# internal CXF reasons.

    rm -f fuse-esb.jks

    keytool -genkey -keyalg RSA \
    -dname "CN=fuse-esb, OU=CXF Webinars, O=fusesource.com, ST=DU, C=IE" \
    -keystore fuse-esb.jks -storetype jks -storepass fuse-esb -keypass bse-esuf \
    -alias fuse-esb 

    keytool -certreq -keystore fuse-esb.jks -storetype jks -storepass fuse-esb \
    -keypass bse-esuf -file csrfuse-esb.pem -alias fuse-esb


    rm -f scott.jks

    keytool -genkey -keyalg RSA \
    -dname "CN=scott, OU=CXF Webinars, O=fusesource.com, ST=DU, C=IE" \
    -keystore scott.jks -storetype jks -storepass scott123 -keypass ttocs123 \
    -alias scott

    keytool -certreq -keystore scott.jks -storetype jks -storepass scott123 \
    -keypass ttocs123 -file csrscott.pem -alias scott


# Have the CN=CA issue a certificate for CN=fuse-esb and CN=scott via
# their Certificate Requests.

   openssl ca -batch -days 7300 -cert cacert.pem -keyfile caprivkey.pem \
   -in csrfuse-esb.pem -out fuse-esb-cert.pem 
   
   openssl ca -batch -days 7300 -cert cacert.pem -keyfile caprivkey.pem \
   -in csrscott.pem -out scott-cert.pem


# Rewrite the certificates in PEM only format. This allows us to concatenate
# them into chains.

    openssl x509 -in cacert.pem -out cacert.pem -outform PEM
    openssl x509 -in fuse-esb-cert.pem -out fuse-esb-cert.pem -outform PEM
    openssl x509 -in scott-cert.pem -out scott-cert.pem -outform PEM

# Create a chain readable by CertificateFactory.getCertificates.

    cat fuse-esb-cert.pem cacert.pem > fuse-esb.chain
    cat scott-cert.pem cacert.pem > scott.chain

# Replace the certificate in the fuse-esb keystore with their respective
# full chains.

    keytool -import -file fuse-esb.chain -keystore fuse-esb.jks -storetype jks \
    -storepass fuse-esb -keypass bse-esuf -noprompt -alias fuse-esb

    keytool -import -file scott.chain -keystore scott.jks -storetype jks \
    -storepass scott123 -keypass ttocs123 -noprompt -alias scott

# Create the Truststore file containing the CA cert.

    rm -f truststore.jks
    
    keytool -import -file cacert.pem -alias TheCA -keystore truststore.jks \
    -storepass truststore -noprompt

#    keytool -import -file scott-cert.pem -alias scott -keystore truststore.jks \
#    -storepass truststore -noprompt

# Get rid of everything not required.
rm -rf *.pem exts demoCA *pk12 *chain
