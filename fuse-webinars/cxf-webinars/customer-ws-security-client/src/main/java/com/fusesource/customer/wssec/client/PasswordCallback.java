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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.ws.security.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordCallback implements CallbackHandler {

  private static final Logger log = LoggerFactory.getLogger(PasswordCallback.class);
  
  private Map<String, String> passwords =
          new HashMap<String, String>();
  
  public PasswordCallback addUser(String user, String pw) { 
    passwords.put(user, pw);
    return this;
  }

  public void handle(Callback[] clbcks) throws IOException, UnsupportedCallbackException {
    log.debug("Number of callbacks = " + clbcks.length);
  
    for (int i = 0; i < clbcks.length; i++) {
      WSPasswordCallback pc = (WSPasswordCallback) clbcks[i];
      log.info("Setting password for user " + pc.getIdentifier() + " existing password is '" + pc.getPassword() + "'");
      
      String pw = passwords.get(pc.getIdentifier());
      if (pw == null) pw = "";
      
      pc.setPassword(pw);

      return;
    }
  }
}
