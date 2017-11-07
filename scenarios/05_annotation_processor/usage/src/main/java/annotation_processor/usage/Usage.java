package annotation_processor.usage;

import annotation_processor.class_generator.GenClass;
import annotation_processor.text_file_generator.GenTextFile;

@GenTextFile(genTextFileName = "this_is_a_text_file.txt")
@GenClass(genClassName = "ThisIsAClassname")
class Usage {

}