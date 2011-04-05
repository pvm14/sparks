/**
 * Copyright 2011 FuseSource
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.fusesource.customer.wssec.client;

import com.fusesource.demo.customer.Customer;
import com.fusesource.demo.wsdl.customerservice.CustomerService;
import com.fusesource.demo.wsdl.customerservice.CustomerService_Service;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.namespace.QName;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;

public class Main {

  public static boolean sign = false;
  public static boolean encrypt = false;
  public static boolean usernameToken = false;
  public static boolean timestamp = false;
  public static boolean passwordDigest = false;
  public static boolean disableCNCheck = true;
  public static String sigKsLoc;
  public static String sigKsPw;
  public static String sigCertAlias;
  public static String sigCertPw;
  public static String encKsLoc;
  public static String encKsPw;
  public static String encCertAlias;
  public static String user = "";
  public static String pw = "";
  public static final String TIMESTAMP_AND_BODY =
          "{Element}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;"
          + "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body";

  public static void main(String args[]) throws Exception {

    try {
      CommandLine cli = new PosixParser().parse(opts, args);

      timestamp = cli.hasOption("timestamp");
      encrypt = cli.hasOption("encrypt");
      sign = cli.hasOption("sign");
      usernameToken = cli.hasOption("username-token");
      passwordDigest = cli.hasOption("password-digest");
      user = cli.getOptionValue("user");
      pw = cli.getOptionValue("pw");
      disableCNCheck = ! cli.hasOption("ecnc");

      if (cli.hasOption("help")
              || !(sign | encrypt | usernameToken | timestamp)) {
        printUsageAndExit();
      }

      if (sign) {
        sigCertAlias = cli.getOptionValue("sa");
        sigCertPw = cli.getOptionValue("spw");
        sigKsLoc = cli.getOptionValue("sk");
        sigKsPw = cli.getOptionValue("skpw");

        if (sigCertAlias == null || sigKsLoc == null || sigKsPw == null || sigCertPw == null) {
          printUsageAndExit("You must provide keystore, keystore password, cert alias and cert password for signing certificate");
        }
      }

      if (encrypt) {
        encCertAlias = cli.getOptionValue("ea");
        encKsLoc = cli.getOptionValue("ek");
        encKsPw = cli.getOptionValue("ekpw");

        if (encCertAlias == null || encKsLoc == null || encKsPw == null) {
          printUsageAndExit("You must provide keystore, keystore password, and cert alias for encryption certificate");
        }
      }

    } catch (ParseException ex) {
      printUsageAndExit();
    }

    // Here we set the truststore for the client - by trusting the CA (in the 
    // truststore.jks file) we implicitly trust all services presenting certificates
    // signed by this CA.
    //
    System.setProperty("javax.net.ssl.trustStore", "../certs/truststore.jks");
    System.setProperty("javax.net.ssl.trustStorePassword", "truststore");

    URL wsdl = new URL("https://localhost:8443/cxf/Customers?wsdl");
    
    // The demo certs provided with this example configure the server with a certificate 
    // called 'fuse-esb'. As this probably won't match the fully-qualified domain
    // name of the machine you're running on, we need to disable Common Name matching
    // to allow the JVM runtime to happily resolve the WSDL for the server. Note that
    // we also have to do something similar on the CXf proxy itself (see below).
    //
    if (disableCNCheck) { 
      HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
        public boolean verify(String string, SSLSession ssls) {
          return true;
        }
      });
    }
    
    Bus bus = SpringBusFactory.newInstance().createBus();
    SpringBusFactory.setDefaultBus(bus);

    Map<String, Object> props = new HashMap<String, Object>();
    props.put(WSHandlerConstants.ACTION, getWSSecActions());

    PasswordCallback passwords = new PasswordCallback();
    props.put(WSHandlerConstants.PW_CALLBACK_REF, passwords);

    if (usernameToken) {
      passwords.addUser(user, pw);
      props.put(WSHandlerConstants.USER, user);
      props.put(WSHandlerConstants.PASSWORD_TYPE, passwordDigest ? "PasswordDigest" : "PasswordText");
    }

    if (encrypt) {
      props.put(WSHandlerConstants.ENCRYPTION_USER, encCertAlias); 
      props.put(WSHandlerConstants.ENC_PROP_REF_ID, "encProps");
      props.put("encProps", merlinCrypto(encKsLoc, encKsPw, encCertAlias));
      props.put(WSHandlerConstants.ENC_KEY_ID, "IssuerSerial");
      props.put(WSHandlerConstants.ENCRYPTION_PARTS, TIMESTAMP_AND_BODY);
    }

    if (sign) {
      props.put(WSHandlerConstants.SIGNATURE_USER, sigCertAlias);
      props.put(WSHandlerConstants.SIG_PROP_REF_ID, "sigProps");
      props.put("sigProps", merlinCrypto(sigKsLoc, sigKsPw, sigCertAlias));
      props.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference");
      props.put(WSHandlerConstants.SIGNATURE_PARTS, TIMESTAMP_AND_BODY);

      passwords.addUser(sigCertAlias, sigCertPw);
    }

    bus.getOutInterceptors().add(new WSS4JOutInterceptor(props));
    bus.getOutInterceptors().add(new LoggingOutInterceptor());

    CustomerService svc = new CustomerService_Service(wsdl).getPort(
            new QName("http://demo.fusesource.com/wsdl/CustomerService/", "SOAPOverHTTP"),
            CustomerService.class);

    // The demo certs provided with this example configure the server with a certificate 
    // called 'fuse-esb'. As this probably won't match the fully-qualified domain
    // name of the machine you're running on, we need to disable Common Name matching
    // to allow the CXF runtime to happily invoke on the server.
    //
    if (disableCNCheck) { 
      HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(svc).getConduit();
      TLSClientParameters tls = new TLSClientParameters();
      tls.setDisableCNCheck(true);
      httpConduit.setTlsClientParameters(tls);
    }
    System.out.println("Looking up the customer...");

    Customer c = svc.lookupCustomer("007");

    System.out.println("Got customer " + c.getFirstName());

  }

  private static String getWSSecActions() {
    StringBuilder sb = new StringBuilder();

    if (usernameToken) {
      sb.append(" UsernameToken ");
    }
    if (timestamp) {
      sb.append(" Timestamp ");
    }
    if (sign) {
      sb.append(" Signature ");
    }
    if (encrypt) {
      sb.append(" Encrypt ");
    }

    return sb.toString();
  }

  private static void printUsageAndExit(String message) {
    if (message != null) {
      System.out.println(message);
    }

    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("client", opts);

    System.exit(0);
  }

  private static void printUsageAndExit() {
    printUsageAndExit(null);
  }
  private static final Options opts = new Options();

  static {
    Option timestamp = OptionBuilder.hasArg(false).
            withDescription("Use Timestamp.").withLongOpt("timestamp").create("t");

    Option usernameToken = OptionBuilder.hasArg(false).
            withDescription("Use UsernameToken").withLongOpt("username-token").create("u");

    Option user = OptionBuilder.hasArg(true).
            withDescription("Username").withLongOpt("username").create("user");

    Option pw = OptionBuilder.hasArg(true).
            withDescription("Password").withLongOpt("password").create("pw");

    Option passwordDigest = OptionBuilder.hasArg(false).
            withDescription("Use PasswordDigest").withLongOpt("password-digest").create("d");

    Option sign = OptionBuilder.hasArg(false).
            withDescription("Sign payload").withLongOpt("sign").create("s");

    Option signatureKeystore = OptionBuilder.hasArg(true).
            withDescription("Keystore with cert for signature generation.").withLongOpt("signature-keystore").create("sk");

    Option signatureKeystorePassword = OptionBuilder.hasArg(true).
            withDescription("Password for signature keystore").withLongOpt("signature-keystore-password").create("skpw");

    Option signatureCertAlias = OptionBuilder.hasArg(true).
            withDescription("Alias for signing certificate in signature keystore.").withLongOpt("signature-cert-alias").create("sa");

    Option signatureCertPassword = OptionBuilder.hasArg(true).
            withDescription("Password for signing certificate's private key.").withLongOpt("signature-cert-password").create("spw");


    Option encryptionKeystore = OptionBuilder.hasArg(true).
            withDescription("Keystore with cert for encryption.").withLongOpt("encryption-keystore").create("ek");

    Option encryptionKeystorePassword = OptionBuilder.hasArg(true).
            withDescription("Password for encryption keystore").withLongOpt("encryption-keystore-password").create("ekpw");

    Option encryptionCertAlias = OptionBuilder.hasArg(true).
            withDescription("Alias for encryption certificate in encryption keystore.").withLongOpt("encryption-cert-alias").create("ea");

    Option encrypt = OptionBuilder.hasArg(false).
            withDescription("Encrypt payload").withLongOpt("encrypt").create("e");
    
    Option enableCNCheck = OptionBuilder.hasArg(false)
            .withDescription("Enable CN (Common Name) check to match the target host with the CN of its certificate.")
            .withLongOpt("--enable-cn-check").create("ecnc");

    Option help = OptionBuilder.withDescription("Prints this message.").withLongOpt("help").create("h");

    opts.addOption(help);
    opts.addOption(timestamp);
    opts.addOption(usernameToken);
    opts.addOption(passwordDigest);
    opts.addOption(sign);
    opts.addOption(encrypt);
    opts.addOption(user);
    opts.addOption(pw);
    opts.addOption(enableCNCheck);
    opts.addOption(signatureKeystore);
    opts.addOption(signatureKeystorePassword);
    opts.addOption(signatureCertAlias);
    opts.addOption(signatureCertPassword);

    opts.addOption(encryptionKeystore);
    opts.addOption(encryptionKeystorePassword);
    opts.addOption(encryptionCertAlias);

  }


  static Properties merlinCrypto(String keystoreLocation, String keystorePassword, String certAlias) {
    Properties p = new Properties();

    p.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
    p.setProperty("org.apache.ws.security.crypto.merlin.file", keystoreLocation);
    p.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
    p.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", certAlias);
    p.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", keystorePassword);

    return p;
  }
}
