/* 
 *  DON'T USE THIS!!
 *
 * My initial attempt to reverse engineer the app behing le.utah.gov was unsuccessful,
 * and reflected in this code.  THIS SCRIPT WILL NOT GET THE LATEST VERSION OF THE STATUTES!!
 *
 * Use the new rust code in rust/
 *
 * Requires groovy, libreoffice, and OOoPy
 */

// these are the firearms-related statutes
firearmsRelated = [
  [ title : '10', chapter: 8, part: 1, section: 47 ],
  [ title : '34', chapter: 45, section: 103 ],
  [ title : '53', chapter: '5a' ],
  [ title : '76', chapter: 1, part: 1, section: 105 ],
  [ title : '76', chapter: 8, part: 3, section: '311.1' ],
  [ title : '76', chapter: 10, part: 5 ],
]

// scrape the page that lists the titles to get the magic v (version?)
def titlespage = new URL("https://le.utah.gov/xcode/C_1800010118000101.html").getText()

titleLinkRE = /href="(Title.*)">/
titleRE = /Title([\d\w]+)\/.*\?v=.*_(.*)/
matcher = (titlespage =~ titleLinkRE)
titles = [: ]
matcher.each {
  m2 = (it[1] =~ titleRE)
  titles[m2[0][1]] = m2[0][2]
}

// now get all the firearms-related sections of the code
allrtfs = ""
firearmsRelated.each {
  if (it.section) { // just get a section
    url =  "https://le.utah.gov/xcode/Title${it.title}/Chapter${it.chapter}/C${it.title}-${it.chapter}-S${it.section}_${titles[it.title]}.rtf"
  }
  else if (it.part) { // get the whole "part"
    url = "https://le.utah.gov/xcode/Title${it.title}/Chapter${it.chapter}/C${it.title}-${it.chapter}-P${it.part}_${titles[it.title]}.rtf"
  }
  else { // get a chapter
    url = "https://le.utah.gov/xcode/Title${it.title}/Chapter${it.chapter}/C${it.title}-${it.chapter}_${titles[it.title]}.rtf"

  }
  rtf = new URL(url).getText()
  
  fname = "T${it.title}_C${it.chapter}_P${it.part}_S${it.section}.rtf"
  
  odtname = "T${it.title}_C${it.chapter}_P${it.part}_S${it.section}.odt"
  allrtfs += " ${odtname}"
  new File(fname) << rtf
  
  locmd = "libreoffice --headless --convert-to odt ${fname}"
  loproc = locmd.execute()
  loproc.waitForProcessOutput(System.out, System.err)
}
catcmd =  "ooo_cat -o UtahFirearmsLaws.odt ${allrtfs} "
println catcmd
catproc = catcmd.execute()
catproc.waitForProcessOutput(System.out, System.err)

pdfcmd = "libreoffice --headless --convert-to pdf UtahFirearmsLaws.odt"
pdfproc = pdfcmd.execute()
pdfproc.waitForProcessOutput(System.out, System.err)
