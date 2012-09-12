package org.sqlproc.sample.simple.model;

public abstract class Media {
	
  public Media() {
  }
  
  public Media(String title) {
  setTitle(title);
  }
  
  private Long id;
    
  public Long getId() {
    return id;
  }
    
  public void setId(Long id) {
    this.id = id;
  }
  
  private String title;
    
  public String getTitle() {
    return title;
  }
    
  public void setTitle(String title) {
    this.title = title;
  }
}