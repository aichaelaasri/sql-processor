package org.sample.dao;

import java.util.List;
import org.sample.model.FunLong;
import org.slf4j.Logger;
import org.sqlproc.engine.SqlControl;
import org.sqlproc.engine.SqlEngineFactory;
import org.sqlproc.engine.SqlSession;
import org.sqlproc.engine.SqlSessionFactory;

@SuppressWarnings("all")
public class FunLongDao {
  protected final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
  
  public FunLongDao() {
  }
  
  public FunLongDao(final SqlEngineFactory sqlEngineFactory) {
    this.sqlEngineFactory = sqlEngineFactory;
  }
  
  public FunLongDao(final SqlEngineFactory sqlEngineFactory, final SqlSessionFactory sqlSessionFactory) {
    this.sqlEngineFactory = sqlEngineFactory;
    this.sqlSessionFactory = sqlSessionFactory;
  }
  
  protected SqlEngineFactory sqlEngineFactory;
  
  protected SqlSessionFactory sqlSessionFactory;
  
  public List<Long> funLong(final SqlSession sqlSession, final FunLong funLong, SqlControl sqlControl) {
    if (logger.isTraceEnabled()) {
    	logger.trace("sql funLong: " + funLong + " " + sqlControl);
    }
    org.sqlproc.engine.SqlProcedureEngine sqlProcFunLongDao = sqlEngineFactory.getCheckedProcedureEngine("PROC_FUN_LONG");
    List<Long> list = sqlProcFunLongDao.callQuery(sqlSession, Long.class, funLong, sqlControl);
    if (logger.isTraceEnabled()) {
    	logger.trace("sql funLong result: " + list);
    }
    return list;
  }
  
  public List<Long> funLong(final FunLong funLong, SqlControl sqlControl) {
    return funLong(sqlSessionFactory.getSqlSession(), funLong, sqlControl);
  }
  
  public List<Long> funLong(final SqlSession sqlSession, final FunLong funLong) {
    return funLong(sqlSession, funLong, null);
  }
  
  public List<Long> funLong(final FunLong funLong) {
    return funLong(funLong, null);
  }
}
