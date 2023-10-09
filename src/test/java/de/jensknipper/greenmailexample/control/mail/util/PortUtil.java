package de.jensknipper.greenmailexample.control.mail.util;

import java.net.ServerSocket;

public final class PortUtil {

  private PortUtil() {}

  public static Integer findRandomOpenPort() {
    try (ServerSocket socket = new ServerSocket(0); ) {
      return socket.getLocalPort();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
