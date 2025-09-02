package com.hotelix.hotel_management.common;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class BaseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public int executeUpdate(String sql, Object... params) {
        Query query = entityManager.createNativeQuery(sql);
        setParameters(query, params);
        return query.executeUpdate();
    }

    @Transactional
    public Object executeInsert(String sql, Object... params) {
        Query query = entityManager.createNativeQuery(sql);
        setParameters(query, params);
        query.executeUpdate();
        // get generated ID (works for MySQL AUTO_INCREMENT)
        return entityManager.createNativeQuery("SELECT LAST_INSERT_ID()").getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> executeQuery(String sql, Object... params) {
        Query query = entityManager.createNativeQuery(sql);
        setParameters(query, params);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object> executeQuerySingleColumn(String sql, Object... params) {
        Query query = entityManager.createNativeQuery(sql);
        setParameters(query, params);
        return query.getResultList();
    }
    private void setParameters(Query query, Object... params) {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]); // JDBC uses 1-based index
            }
        }
    }
}
