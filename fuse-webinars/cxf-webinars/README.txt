These demos have been built and tested using Fuse ESB (ServiceMix) 4.3.1-fuse-00-00

Installing the 'features'
-------------------------
The samples for the cxf-webinars have all been modeled as ServiceMix 'features', that 
is, sets of bundles. To install each feature, you must first register the features
file with ServiceMix. In the ServiceMix shell, try this: 

	features:addUrl mvn:com.fusesource/customer-features/1.0.0/xml

Now do 'features:list | grep customer'. You should see the relevant features listed. Pick
the feature you're interested in, and install it using 'features:install'. For example, you can do

	features:install customer-ws-secure 

To uninstall a feature, you can just do 

	features:uninstall <feature-name>

To install the camel-cxf demos into Fuse ESB, you need to make make sure you 
have installed the 'camel-cxf' feature. Also, you need to install the
'camel-velocity' feature for the payload and provider examples. When you have 
this done, you can install the relevant features. 

Installing the customer-ws-secure web service.
----------------------------------------------
First, you've got copy the certificates to the <servicemix-install>/etc directory. You 
can do this with something like: 

	cp certs/*jks <servicemix-install>/etc

Then, you've got to copy the customer.cfg file to the <servicemix-install>/etc directory.

	cp etc/customer.cfg <servicemix-install>/etc

Now, start up your Fuse ESB (ServiceMix) instance - it will pick up the 'customer' configuration
from customer.cfg, and use this to pick up the correct certificates. To install the secure custoemr 
web service, try: 

	features:install customer-ws-secure

If you like, you can later edit the <servicemix-install>/etc/customer.cfg file, for example, you 
can change the WS-Security settings. If you do so, you must update the customer-ws-secure bundle
to get the changes. To do this, do an 'osgi:list  | grep customer' in the the ServiceMix shell, to 
get the bundle id, and then do 'osgi:update <bundle-id>'. 

Running the secure customer WS client
-------------------------------------
The secure customer client has been designed to be very, very configurable, allowing you to 
specify diffent WS-Security actions (e.g. UsernameToken Timestamp Signature Encrypt). Run the 
client using 'mvn exec:java' to get a full list of the options available. Then, use the '-Dexec.args' 
option to specify the arguments you want to use. For example, to perform the full gamut, you might 
use (from the customer-ws-security-client dir):

	mvn exec:java -Dexec.args="-u -t -user Alice -pw ecilA -sign -sa scott -sk ../certs/scott.jks -skpw scott123 -spw ttocs123 -encrypt -ea fuse-esb -ek ../certs/fuse-esb.jks -ekpw fuse-esb"

