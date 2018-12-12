import javax.xml.xpath.*
import java.net.URI;

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
            outputOdt.addParagraph("Utah Firearms-Related Statutes");

            Paragraph p1 = outputOdt.getParagraphByIndex(0, true);
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
                println it
                convert_xml_file(it, outputOdt);
            }

            outputOdt.save(outFileName);
        } catch (Exception e) {
            println e
            System.err.println("ERROR: unable to create output file.");
        }
    }

    public static void convert_xml_file(String xml_file, TextDocument odt) {
        println "Converting ${xml_file}"
            }


}
