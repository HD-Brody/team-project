package data_access.persistence.in_memory;

import entity.Session;
import use_case.repository.SessionRepository;

public class InMemorySessionInfoDataAccessObject implements SessionRepository {

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
