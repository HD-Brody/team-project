package use_case.port.outgoing;

import entity.Session;

public interface SessionPort {

    void setSession(Session session);

    Session getSession();

}
