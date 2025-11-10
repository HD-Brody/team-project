package use_case.port.outgoing;

/**
 * Provides a boundary for managing transactions across repositories.
 */
public interface TransactionalPersistencePort {
    <T> T executeInTransaction(TransactionCallback<T> callback);

    interface TransactionCallback<T> {
        T doInTransaction();
    }
}
