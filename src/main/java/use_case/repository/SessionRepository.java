package use_case.repository;

import entity.Session;

public interface SessionRepository {

    void setSession(Session session);

    Session getSession();

}
