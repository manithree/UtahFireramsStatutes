import javax.xml.xpath.*
import java.net.URI;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.style.TOCStyle;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;

class xml2odf {

    public static int indent_width = 5;

    public static void main(String[] args) {
        def outFileName = args[-1];

        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();
            // add paragraph
            Paragraph p1 = outputOdt.addParagraph("Utah Firearms-Related Statutes");

            p1.applyHeading(false, 0);

            TextTableOfContentElement textTableOfContentElement = outputOdt.createDefaultTOC(p1,false);
            outputOdt.addPageBreak()
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
        def statute = new XmlParser().parse(new File(xml_file))

        if (statute.name() == "chapter") {
            convert_chapter(statute, odt)
        }
        else if (statute.name() == "part") {
            convert_part(statute, odt)
        }
        else { // there are other options, but for now ..
            convert_section(statute, odt)
        }
    }

    public static void convert_chapter(Node el, TextDocument odt) {
        generate_heading(el, 1, odt)
        el.section.each() {
            convert_section(it, odt)
        }
        odt.addPageBreak()
    }

    public static void convert_part(Node el, TextDocument odt) {
        generate_heading(el, 2, odt)
        el.section.each() {
            convert_section(it, odt)
        }
        odt.addPageBreak()
    }

    public static void convert_section(Node el, TextDocument odt) {
        generate_heading(el, 3, odt)
        el.subsection.each() {
            generate_subsection(it, el.@number.toString(), odt, 0)
        }
    }

    public static void generate_subsection(Node el, String parent_num, TextDocument odt, int level) {
        def disp_num = el.@number.toString()[parent_num.length()..-1]
        def local_text = ""
        println "${el.@number}: localText.size(): ${el.localText().size()}"
        el.localText().each() {
            local_text += it.trim()
        }
        Paragraph p = odt.addParagraph("${disp_num} ${local_text}")
        def p_el = p.getOdfElement()
        p_el.setProperty(OdfParagraphProperties.MarginLeft, "${level*indent_width}mm");
        el.subsection.each() {
            generate_subsection(it, el.@number.toString(), odt, level+1)
        }
    }

    public static void generate_heading(Node element, int heading_level, TextDocument odt) {
        //println "Heading: ${element.@number} ${element.catchline.text()}"
        Paragraph p = odt.addParagraph("${element.@number} ${element.catchline.text()}");
        if (element.effdate != null && element.effdate.toString().length() > 0) {
            odt.addParagraph("(Effective ${element.effdate})")
        }
        p.applyHeading(true, heading_level)
    }

}
