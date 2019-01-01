import javax.xml.xpath.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URI;

/*************************************************************
 * Simple groovy class to convert le.utah.gov's xml format
 * to html.
 *************************************************************/

class xml2html {

    public static int indent_width = 3;

    public static void main(String[] args) {
        def outFileName = args[-1];

        def toc = new StringWriter();
        def body = new StringWriter();
        try {
            args[0..-2].each() {
                convert_xml_file(it, toc, body);
            }
            new File(outFileName).newWriter().withWriter { w ->
                def today = new Date();
                def sdf = new SimpleDateFormat("yyyy-MM-dd")
                w << """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Utah Firearms Statutes ${sdf.format(today)}</title>
<style>
html,body{font-size:100%}
h1{font-size:2.125em; margin-left: 0%}
h2{font-size:1.6875em; margin-left: 3%}
h3{font-size:1.375em; margin-left: 6%}
h4{font-size:1.125em; margin-left: 9%}
h5{font-size:1.125em; margin-left: 12%}
h6{font-size:1em; margin-left: 15%}
hr{border:solid #ddddd8;border-width:1px 0 0;clear:both;margin:1.25em 0 1.1875em;height:0}
</style>
</head>
<body>
"""

                w << toc
                w << body
                w << """
</body>
</html>
"""
            }
        } catch (Exception e) {
            println e
            org.codehaus.groovy.runtime.StackTraceUtils.sanitize(new Exception(e)).printStackTrace()
        }
    }

    public static void convert_xml_file(String xml_file, Writer toc, Writer body)
    {
        println "Converting ${xml_file}"
        def statute = new XmlParser().parse(new File(xml_file))

        if (statute.name() == "chapter") {
            convert_chapter(statute, toc, body)
        }
        else if (statute.name() == "part") {
            convert_part(statute, toc, body)
        }
        else { // there are other options, but for now ..
            convert_section(statute, toc, body)
        }
    }

    public static void convert_chapter(Node el, Writer toc, Writer body) {
        generate_heading(el, 1, toc, body)
        el.section.each() {
            convert_section(it, toc, body)
        }
        body.append("<hr/>")
    }

    public static void convert_part(Node el, Writer toc, Writer body) {
        generate_heading(el, 2, toc, body)
        el.section.each() {
            convert_section(it, toc, body)
        }
        body.append("<br/>")
    }

    public static void convert_section(Node el, Writer toc, Writer body) {
        generate_heading(el, 3, toc, body)
        if (el.subsection.size() < 1) {
            // treat as a subsection:
            generate_subsection(el, "", toc, body, 3)
        }
        else {
            el.subsection.each() {
                generate_subsection(it, el.@number.toString(), toc, body, 3)
            }
        }
    }

    public static void generate_subsection(Node el, String parent_num, Writer toc, Writer body, int level) {
        def disp_num = el.@number.toString()[parent_num.length()..-1]
        def local_text = ""
        body.append("<p style=\"display: block; margin-left: ${(level-1)*indent_width}%;\">${disp_num} ")
        el.children().each() { child ->
            if (child in Node) {
                if (child.name() == "xref") {
                    body.append(child.text().trim() + " ")
                }
                else if (child.name() == "subsection") {
                    generate_subsection(child, el.@number.toString(), toc, body, level+1)
                }
                else if (child.name() == "histories") {
                    // generate histories eventually?
                }
                else if (child.name() == "catchline") {
                    // ignore
                }
                else if (child.name() == "tab") {
                    // ignore
                }
                else {
                    println "Unrecognized node type ${child.name()} in ${el.@number}"
                }
            }
            else if (child in String) {
                body.append(child.trim() + " ")
            }
            else {
                println "Unrecognized child of subsection ${el.@number}: ${child.getClass()}"
            }
        }
        body.append("</p>\n")
    }

    public static void generate_heading(Node element, int heading_level, Writer toc, Writer body) {
        toc.append("<a style=\"margin-left: ${(heading_level-1)*indent_width}%;\" href=\"#_${element.@number}\">${element.@number} ${element.catchline.text()}</a><br/>\n")
        body.append("<h${heading_level} id=\"_${element.@number}\">${element.@number} ${element.catchline.text()}</h${heading_level}>\n")
        if (element.effdate != null && element.effdate.size() > 0) {
            body.append("<p style=\"display: block; margin-left: ${(heading_level-1)*indent_width}%;\">(Effective ${element.effdate[0].text()})</p>\n")
        }
    }

}
