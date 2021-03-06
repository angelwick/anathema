package net.sf.anathema.character.generic.impl.magic.persistence.builder.prerequisite;

import net.sf.anathema.character.generic.traits.IGenericTrait;
import net.sf.anathema.lib.exception.PersistenceException;
import org.dom4j.Element;

public interface ITraitPrerequisitesBuilder {

  IGenericTrait[] buildTraitPrerequisites(Element prerequisiteListElement) throws PersistenceException;
}