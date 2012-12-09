/*
 * generated by Xtext
 */
package org.sqlproc.dsl.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.generator.IFileSystemAccess
import org.sqlproc.dsl.processorDsl.PojoEntity
import com.google.inject.Inject
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.sqlproc.dsl.processorDsl.PojoProperty
import org.eclipse.xtext.xbase.compiler.ImportManager

import static org.sqlproc.dsl.util.Utils.*;
import java.util.ArrayList
import org.sqlproc.dsl.processorDsl.Implements
import org.sqlproc.dsl.processorDsl.Extends

class ProcessorDslGenerator implements IGenerator {
	
@Inject extension IQualifiedNameProvider
	
override void doGenerate(Resource resource, IFileSystemAccess fsa) {
	for(e: resource.allContents.toIterable.filter(typeof(PojoEntity))) {
		fsa.generateFile(e.eContainer.fullyQualifiedName.toString("/") + "/"+
			e.fullyQualifiedName + ".java",e.compile
		)
	}
}

def compile(PojoEntity e) '''
«val importManager = new ImportManager(true)»
«addImplements(e, importManager)»
«addExtends(e, importManager)»
«val classBody = compile(e, importManager)»
«IF e.eContainer != null»package «e.eContainer.fullyQualifiedName»;«ENDIF»
  «IF !importManager.imports.empty»
  
  «FOR i : importManager.imports»
import «i»;
  «ENDFOR»
  «ENDIF»
  «IF getSernum(e) != null»

import java.io.Serializable;
  «ENDIF»
  «IF !e.listFeatures.empty»
import java.util.ArrayList;
  «ENDIF»
  «IF e.hasIsNull != null || e.hasIsInit != null»
import java.util.Set;
import java.util.HashSet;
  «ENDIF»

«classBody»
'''

def compile(PojoEntity e, ImportManager importManager) '''
public «IF isAbstract(e)»abstract «ENDIF»class «e.name» «compileExtends(e)»«compileImplements(e)»{
  «IF getSernum(e) != null»
  
  private static final long serialVersionUID = «getSernum(e)»L;
  «ENDIF»
	
  public «e.name»() {
  }
  «IF !e.requiredFeatures.empty»
  
  public «e.name»(«FOR f:e.requiredFeatures SEPARATOR ", "»«f.compileType(importManager)» «f.name»«ENDFOR») {
  «FOR f:e.requiredSuperFeatures BEFORE "  super(" SEPARATOR ", " AFTER ");"»«f.name»«ENDFOR»
  «FOR f:e.requiredFeatures1 SEPARATOR "
"»  this.«f.name» = «f.name»;«ENDFOR»
  }
  «ENDIF»
  «FOR f:e.features.filter(x| isAttribute(x))»
    «f.compile(importManager, e)»
  «ENDFOR»
  «FOR f:e.features.filter(x| !isAttribute(x))»«IF f.name.equalsIgnoreCase("hashCode")»«f.compileHashCode(importManager, e)»
  «ELSEIF f.name.equalsIgnoreCase("equals")»«f.compileEquals(importManager, e)»
  «ELSEIF f.name.equalsIgnoreCase("isInit")»«f.compileIsInit(importManager, e)»
  «ELSEIF f.name.equalsIgnoreCase("isNull")»«f.compileIsNull(importManager, e)»
  «ELSEIF f.name.equalsIgnoreCase("toString")»«f.compileToString(importManager, e)»«ENDIF»«ENDFOR»
}
'''

def compile(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    private «f.compileType(importManager)» «f.name»«IF isList(f)» = new Array«f.compileType(importManager)»()«ENDIF»;
  
    public «f.compileType(importManager)» get«f.name.toFirstUpper»() {
      return «f.name»;
    }
  
    public void set«f.name.toFirstUpper»(«f.compileType(importManager)» «f.name») {
      this.«f.name» = «f.name»;
    }
  
    public «e.name» _set«f.name.toFirstUpper»(«f.compileType(importManager)» «f.name») {
      this.«f.name» = «f.name»;
      return this;
    }
'''

def compileHashCode(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      «FOR f2:f.attrs»
      result = prime * result + (int) («f2.name» ^ («f2.name» >>> 32));
      «ENDFOR»
      return result;
    }  
'''

def compileEquals(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      «e.name» other = («e.name») obj;
      «FOR f2:f.attrs»
      if («f2.name» != other.«f2.name»)
        return false;
      «ENDFOR»
      return true;
    }  
'''

def compileToString(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    @Override
    public String toString() {
      return "«e.name» [«FOR f2:f.simplAttrs SEPARATOR " + \", "»«f2.name»=" + «f2.name»«ENDFOR»«IF getSuperType(e) != null» + super.toString()«ENDIF» + "]";
    }

    public String toStringFull() {
      return "«e.name» [«FOR f2:f.attrs SEPARATOR " + \", "»«f2.name»=" + «f2.name»«ENDFOR»«IF getSuperType(e) != null» + super.toString()«ENDIF» + "]";
    }
'''

def compileIsNull(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    private Set<String> nullValues = new HashSet<String>();

    public enum Attribute {
      «FOR f2:f.attrs SEPARATOR ", "»«f2.name»«ENDFOR»
    }

    public void setNull(Attribute... attributes) {
      if (attributes == null)
        throw new IllegalArgumentException();
      for (Attribute attribute : attributes)
        nullValues.add(attribute.name());
    }

    public void clearNull(Attribute... attributes) {
      if (attributes == null)
        throw new IllegalArgumentException();
      for (Attribute attribute : attributes)
        nullValues.remove(attribute.name());
    }

    public Boolean isNull(String attrName) {
      if (attrName == null)
        throw new IllegalArgumentException();
      return nullValues.contains(attrName);
    }

    public Boolean isNull(Attribute attribute) {
      if (attribute == null)
        throw new IllegalArgumentException();
      return nullValues.contains(attribute.name());
    }

    public void clearAllNull() {
      nullValues = new HashSet<String>();
    }
'''

def compileIsInit(PojoProperty f, ImportManager importManager, PojoEntity e) '''

    private Set<String> initAssociations = new HashSet<String>();

    public enum Association {
      «FOR f2:f.attrs SEPARATOR ", "»«f2.name»«ENDFOR»
    }

    public void setInit(Association... associations) {
      if (associations == null)
        throw new IllegalArgumentException();
      for (Association association : associations)
        initAssociations.add(association.name());
    }

    public void clearInit(Association... associations) {
      if (associations == null)
        throw new IllegalArgumentException();
      for (Association association : associations)
        initAssociations.remove(association.name());
    }

    public Boolean isInit(String attrName) {
      if (attrName == null)
        throw new IllegalArgumentException();
      return initAssociations.contains(attrName);
    }

    public Boolean isInit(Association association) {
      if (association == null)
        throw new IllegalArgumentException();
      return initAssociations.contains(association.name());
    }

    public void clearAllInit() {
      initAssociations = new HashSet<String>();
    }
'''

def compileType(PojoProperty f, ImportManager importManager) '''
  «IF f.getNative != null»«f.getNative.substring(1)»«ELSEIF f.getRef != null»«f.getRef.fullyQualifiedName»«ELSEIF f.getType != null»«importManager.serialize(f.getType)»«ENDIF»«IF f.getGtype != null»<«importManager.serialize(f.getGtype)»>«ENDIF»«IF f.getGref != null»<«f.getGref.fullyQualifiedName»>«ENDIF»«IF f.array»[]«ENDIF»'''
  
def listFeatures(PojoEntity e) {
	
   	val list = new ArrayList<PojoProperty>()
	if (getSuperType(e) != null)
	  list.addAll(getSuperType(e).listFeatures)
	list.addAll(e.listFeatures1)
    return list
}

def listFeatures1(PojoEntity e) {
	return e.features.filter(f|isList(f)).toList
}
  
def requiredFeatures(PojoEntity e) {
	
   	val list = new ArrayList<PojoProperty>()
	if (getSuperType(e) != null)
	  list.addAll(getSuperType(e).requiredFeatures)
	list.addAll(e.requiredFeatures1)
    return list
}

def requiredSuperFeatures(PojoEntity e) {
	
   	val list = new ArrayList<PojoProperty>()
	if (getSuperType(e) != null)
	  list.addAll(getSuperType(e).requiredFeatures)
    return list
}

def requiredFeatures1(PojoEntity e) {
	return e.features.filter(f|isRequired(f)).toList
}

def hasIsNull(PojoEntity e) {
	return e.features.findFirst(f|f.name == "isNull")
}

def hasIsInit(PojoEntity e) {
	return e.features.findFirst(f|f.name == "isInit")
}

def isAttribute(PojoProperty f) {
    return f.getNative != null || f.getRef != null || f.getType != null
}

def simplAttrs(PojoProperty f) {
	return f.attrs.filter(f2|f2.getNative != null || f2.getType != null).toList
}

def compileExtends(PojoEntity e) '''
	«IF getSuperType(e) != null»extends «getSuperType(e).fullyQualifiedName» «ELSEIF getExtends(e) != ""»extends «getExtends(e)» «ENDIF»'''

def compileImplements(PojoEntity e) '''
	«IF isImplements(e) || getSernum(e) != null»implements «FOR f:e.eContainer.eContents.filter(typeof(Implements)) SEPARATOR ", " »«f.getImplements().simpleName»«ENDFOR»«IF getSernum(e) != null»«IF isImplements(e)», «ENDIF»Serializable«ENDIF» «ENDIF»'''

def compile(Extends e, ImportManager importManager) {
	importManager.addImportFor(e.getExtends())
}

def addImplements(PojoEntity e, ImportManager importManager) {
	for(impl: e.eContainer.eContents.filter(typeof(Implements))) {
		importManager.addImportFor(impl.getImplements())
	}
}

def addExtends(PojoEntity e, ImportManager importManager) {
	for(ext: e.eContainer.eContents.filter(typeof(Extends))) {
		importManager.addImportFor(ext.getExtends())
	}
}

def getExtends(PojoEntity e) {
	for(ext: e.eContainer.eContents.filter(typeof(Extends))) {
		return ext.getExtends().simpleName
	}
	return ""
}

def isImplements(PojoEntity e) {
	for(ext: e.eContainer.eContents.filter(typeof(Implements))) {
		return true
	}
	return false
}
}