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
package com.fusesource.customer.ws;

import java.io.IOException;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.ws.security.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordCallback implements CallbackHandler {

  private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
  private Map<String, String> passwords;

  public PasswordCallback(Map<String, String> passwords) {
    this.passwords = passwords;
  }

  public void handle(Callback[] clbcks) throws IOException, UnsupportedCallbackException {
    boolean authenticated = false;

    for (Callback c : clbcks) {
      WSPasswordCallback pc = (WSPasswordCallback) c;

      if (pc.getPassword() != null) {
        try {
          LoginContext jaas =
            new LoginContext("karaf", 
                  new ProvideUsernamePassword(pc.getIdentifier(), pc.getPassword()));
          jaas.login();
          authenticated = true;
          Subject s = jaas.getSubject();
          return;
        } catch (LoginException ex) {
          log.warn("Failed to authenticate user '" + pc.getIdentifier() + "' using JAAS.");
          throw new IOException("Failed to authenticate user '" + pc.getIdentifier() + "' using JAAS.");
        }
      } else {
        String pass = passwords.get(pc.getIdentifier());
        if (pass == null) {
          log.warn("Failed to retrieve password for user '" 
                  + pc.getIdentifier() + "' from store passwords.");
          throw new IOException("Failed to retrieve password for user '" 
                  + pc.getIdentifier() + "' from store passwords.");
        }
        pc.setPassword(pass);
        log.debug("Returning password '" + pass + "'");
        return;
      }
    }

    //
    // Password not found
    //
    throw new IOException();
  }

  private class ProvideUsernamePassword implements CallbackHandler {

    private String username;
    private String password;

    public ProvideUsernamePassword(String username, String password) {
      this.username = username;
      this.password = password;
    }

    public void handle(Callback[] clbcks) throws IOException, UnsupportedCallbackException {
      for (Callback clbck : clbcks) {
        if (clbck instanceof javax.security.auth.callback.PasswordCallback) {
          ((javax.security.auth.callback.PasswordCallback) clbck).setPassword(password.toCharArray());
        } else if (clbck instanceof NameCallback) {
          ((NameCallback) clbck).setName(username);
        }
      }
    }
  }
}
