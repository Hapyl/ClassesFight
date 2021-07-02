package ru.hapyl.classesfight.classes.iclass.inst;

import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.iclass.IClass;
import ru.hapyl.classesfight.classes.iclass.Librarian;

public interface Instance<T extends IClass> {

	Librarian LIBRARIAN = ((Librarian)ClassManager.LIBRARIAN.getTheClass());

	T getInstance() throws CommandLine.InitializationException;

}
