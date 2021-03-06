package org.sqlproc.sample.simple.dao;

import java.util.List;
import org.sqlproc.engine.SqlControl;
import org.sqlproc.engine.SqlRowProcessor;
import org.sqlproc.engine.SqlSession;
import org.sqlproc.sample.simple.dao.BaseDao;
import org.sqlproc.sample.simple.model.PersonLibrary;

@SuppressWarnings("all")
public interface PersonLibraryDao extends BaseDao {
  public PersonLibrary insert(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public PersonLibrary insert(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public PersonLibrary insert(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public PersonLibrary insert(final PersonLibrary personLibrary);
  
  public PersonLibrary get(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public PersonLibrary get(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public PersonLibrary get(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public PersonLibrary get(final PersonLibrary personLibrary);
  
  public int update(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int update(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int update(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public int update(final PersonLibrary personLibrary);
  
  public int delete(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int delete(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int delete(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public int delete(final PersonLibrary personLibrary);
  
  public List<PersonLibrary> list(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public List<PersonLibrary> list(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public List<PersonLibrary> list(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public List<PersonLibrary> list(final PersonLibrary personLibrary);
  
  public int query(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl, final SqlRowProcessor<PersonLibrary> sqlRowProcessor);
  
  public int query(final PersonLibrary personLibrary, SqlControl sqlControl, final SqlRowProcessor<PersonLibrary> sqlRowProcessor);
  
  public int query(final SqlSession sqlSession, final PersonLibrary personLibrary, final SqlRowProcessor<PersonLibrary> sqlRowProcessor);
  
  public int query(final PersonLibrary personLibrary, final SqlRowProcessor<PersonLibrary> sqlRowProcessor);
  
  public int count(final SqlSession sqlSession, final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int count(final PersonLibrary personLibrary, SqlControl sqlControl);
  
  public int count(final SqlSession sqlSession, final PersonLibrary personLibrary);
  
  public int count(final PersonLibrary personLibrary);
}
