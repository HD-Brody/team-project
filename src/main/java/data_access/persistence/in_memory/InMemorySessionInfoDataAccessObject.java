package data_access.persistence.in_memory;

import entity.Session;
import use_case.port.outgoing.SessionPort;

public class InMemorySessionInfoDataAccessObject implements SessionPort {

    private Session session;

    public InMemorySessionInfoDataAccessObject() {
        session = null;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public Session getSession() {
        return session;
    }
}
