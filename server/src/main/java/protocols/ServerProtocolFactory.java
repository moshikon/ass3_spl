package protocols;

import database.EchoProtocol;

public interface ServerProtocolFactory {
   EchoProtocol create();
}
