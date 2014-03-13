package com.google.games.bloopyfish.config;

import com.google.games.bloopyfish.rest.GsonMessageBodyHandler;
import com.google.games.bloopyfish.rest.GuestbookResource;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class Resources extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<Class<?>>();
    s.add(GuestbookResource.class);
    s.add(GsonMessageBodyHandler.class);
    return s;
  }
}
