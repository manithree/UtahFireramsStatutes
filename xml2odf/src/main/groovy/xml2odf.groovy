import javax.xml.xpath.*
import java.net.URI;
import groovy.util.slurpersupport.GPathResult;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.style.TOCStyle;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;

class xml2odf {

    public static void main(String[] args) {
        def outFileName = args[-1];

        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();
            // add paragraph
            Paragraph p1 = outputOdt.addParagraph("Utah Firearms-Related Statutes");

            //Paragraph p1 = outputOdt.getParagraphByIndex(0, true);
//            Paragraph p2 = outputOdt.getParagraphByIndex(1, true);
//            Paragraph p3 = outputOdt.getParagraphByIndex(2, true);
          p1.applyHeading(true, 0);
//            p2.applyHeading(true, 2);
//            p3.applyHeading(true, 3);

            TOCStyle tocstyle = new TOCStyle();
            tocstyle.addStyle("User_20_Index_20_1", 1);
            tocstyle.addStyle("User_20_Index_20_2", 2);
            tocstyle.addStyle("User_20_Index_20_3", 3);
            tocstyle.addStyle("User_20_Index_20_4", 4);
            tocstyle.addStyle("User_20_Index_20_5", 5);
            tocstyle.addStyle("User_20_Index_20_6", 6);
            tocstyle.addStyle("User_20_Index_20_7", 7);
            tocstyle.addStyle("User_20_Index_20_8", 8);
            tocstyle.addStyle("User_20_Index_20_9", 9);
            tocstyle.addStyle("User_20_Index_20_10", 10);
            TextTableOfContentElement textTableOfContentElement = outputOdt.createTOCwithStyle(p1, tocstyle, false);

            args[0..-2].each() {
                convert_xml_file(it, outputOdt);
            }

            outputOdt.save(outFileName);
        } catch (Exception e) {
            println e
            org.codehaus.groovy.runtime.StackTraceUtils.sanitize(new Exception(e)).printStackTrace()
        }
    }

    public static void convert_xml_file(String xml_file, TextDocument odt)
    {
        println "Converting ${xml_file}"
        def statute = new groovy.util.XmlSlurper().parse(new File(xml_file))
        //        println statute.@number
        //            println statute.effdate
        //        println statute.name()
        //        statute.children().each() {
        //            println it
        //        }
        
        if (statute.name() == "chapter") {
            convert_chapter(statute, odt)
        }
        else if (statute.name() == "part") {
            convert_part(statute, odt)
        }
        else { // there are other options, but for now ..
            convert_section(statute, odt)
        }
        //println statute.name()
        //println statute.catchline.text()
    }

    public static void convert_chapter(GPathResult el, TextDocument odt) {
        println "Generating chapter"
        generate_heading(el, 1, odt)
        el.section.each() {
            convert_section(it, odt)
        }
    }

    public static void convert_part(GPathResult el, TextDocument odt) {
        println "Generating part"
        generate_heading(el, 2, odt)
        el.section.each() {
            convert_section(it, odt)
        }
    }

    public static void convert_section(GPathResult el, TextDocument odt) {
        println "Generating section"
        generate_heading(el, 3, odt)
        el.subsection.each() {
            generate_subsection(it, el.@number.toString(), odt)
        }
    }

    public static void generate_subsection(GPathResult el, String parent_num, TextDocument odt) {
        Paragraph p = odt.addParagraph("(${el.@number}) ${el.text()}")
    }

    public static void generate_heading(GPathResult element, int heading_level, TextDocument odt) {
        println "Heading: ${element.@number} ${element.catchline.text()}"
        Paragraph p = odt.addParagraph("${element.@number} ${element.catchline.text()}");
        if (element.effdate) {
            odt.addParagraph("(Effective ${element.effdate})")
        }
        p.applyHeading(true, heading_level)
    }

}
