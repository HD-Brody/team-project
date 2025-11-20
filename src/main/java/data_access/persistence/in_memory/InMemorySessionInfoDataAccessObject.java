package data_access.persistence.in_memory;

import entity.Session;
import use_case.port.outgoing.SessionPort;

public class InMemorySessionInfoDataAccessObject implements SessionPort {

    private Session session;

    public InMemorySessionInfoDataAccessObject() {
        session = null;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
}
