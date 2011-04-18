DESCRIPTION:
================
This demo demonstrates the use of bundle fragments within the ESB.

We have two projects:

hello-world - A regular osgi bundle.
hello-fragment - An optional fragment bundle. Its not usable without its host (it requires a fragment host to run).

Important points to note about the example:

1) The hello-world bundle is not aware of its fragments. 

2) The hello-fragment declares its dependency on the host using the Fragment-Host manifest header in its pom.xml.

    <plugins>
        <plugin>
            <groupId>org.apache.felix</groupId>
            <artifactId>maven-bundle-plugin</artifactId>
            <extensions>true</extensions>
            <version>1.4.0</version>
            <configuration>
                <instructions>
                    <Export-Package>
                        com.fusesource.support.osgi.helloworld*
                    </Export-Package>
                 
                    <Import-Package>*</Import-Package>
                    <Include-Resource>src/main/resources</Include-Resource>
					<Fragment-Host>com.fusesource.osgi-test-hello-world-bundle</Fragment-Host>
                </instructions>
			
            </configuration>
        </plugin>
    </plugins>

  This is the symbolic name of the host bundle. If we look at the MANIFEST.MF of the hello-world bundle, you
  will see the string to use is the "Bundle-SymbolicName"

Manifest-Version: 1.0
Export-Package: com.fusesource.support.osgi.helloworld
Private-Package: com.fusesource.support.osgi.helloworld.internal
Ignore-Package: com.fusesource.support.osgi.helloworld.internal
Built-By: dstanley
Tool: Bnd-0.0.238
Bundle-Name: osgi-test-hello-world-bundle
Created-By: Apache Maven Bundle Plugin
Build-Jdk: 1.6.0_24
Bundle-Version: 0.0.1.SNAPSHOT
Bnd-LastModified: 1303140813373
Bundle-ManifestVersion: 2
Import-Package: com.fusesource.support.osgi.helloworld,org.slf4j;versi
 on="1.4",org.springframework.beans.factory;version="2.5"
Bundle-SymbolicName: com.fusesource.osgi-test-hello-world-bundle



PREREQUISITES:
================
Fuse ESB version 4.3.x or later

COMPILING:
================

>cd hello-world
>mvn install

>cd hello-fragment
>mvn install


RUNNING:
================

You can determine the correct maven url syntax using: mvn:groupId/artifactId[/[version]/[type]]

On the Fuse ESB OSGi console run:

>osgi:install mvn:com.fusesource/osgi-test-hello-world-fragment/0.0.1-SNAPSHOT/jar
>osgi:install -s mvn:com.fusesource/osgi-test-hello-world-bundle/0.0.1-SNAPSHOT/jar




OUTPUT:
================

>osgi:list

[ 223] [Resolved   ] [            ] [       ] [   60] osgi-test-hello-world-fragment (0.0.1.SNAPSHOT)
                                       Hosts: 224
[ 224] [Active     ] [            ] [Started] [   60] osgi-test-hello-world-bundle (0.0.1.SNAPSHOT)
                                       Fragments: 223

Note that when we start our hello-world bundle the Fragment is automatically resolved. Its noted in the output.

Looking at the logs, indicates that the fragment has been successfully resolved. We see the string contributed
by the fragment in the log.

>log:display

10:56:35,135 | INFO  | ExtenderThread-8 | DefaultListableBeanFactory       | ?                                   ? | 65 - org.springframework.beans - 3.0.5.RELEASE | Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@65694ee6: defining beans [helloworld.cfg.with.defaults,org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#0,org.springframework.osgi.service.exporter.support.OsgiServiceFactoryBean#0,helloWorld]; root of factory hierarchy
10:56:35,141 | INFO  | ExtenderThread-8 | HelloWorldImpl                   | ?                                   ? | 220 - com.fusesource.osgi-test-hello-world-bundle - 0.0.1.SNAPSHOT | Setting the response to 'Right back at ya'
10:56:35,142 | INFO  | ExtenderThread-8 | HelloWorldImpl                   | ?                                   ? | 220 - com.fusesource.osgi-test-hello-world-bundle - 0.0.1.SNAPSHOT | HelloWorldImpl initialized.
10:56:35,142 | INFO  | ExtenderThread-8 | HelloWorldImpl                   | ?                                   ? | 220 - com.fusesource.osgi-test-hello-world-bundle - 0.0.1.SNAPSHOT | Will return response 'Right back at ya'
10:56:35,142 | INFO  | ExtenderThread-8 | HelloWorldImpl                   | ?                                   ? | 220 - com.fusesource.osgi-test-hello-world-bundle - 0.0.1.SNAPSHOT | Contributed (via Fragment) is 'Fragment response string'
10:56:35,143 | INFO  | ExtenderThread-8 | OsgiServiceFactoryBean           | ?                                   ? | 79 - org.springframework.osgi.core - 1.2.1 | Publishing service under classes [{com.fusesource.support.osgi.helloworld.HelloWorld}]
10:56:35,144 | INFO  | ExtenderThread-8 | OsgiBundleXmlApplicationContext  | ?