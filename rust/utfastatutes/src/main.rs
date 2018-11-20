use std::vec::Vec;
use std::collections::HashMap;

extern crate reqwest;
extern crate regex;

use regex::Regex;

#[derive(Debug)]
struct CodeRef <'a> {
    title: &'a str,
    chapter: &'a str,
    part: Option<&'a str>,
    section: Option<&'a str>
}

fn get_title_map(page: &String) -> HashMap<String, String> {
    let mut title_map:  HashMap<String, String> = HashMap::new();
    let link_re = Regex::new(r#"href="(Title.*)">"#).unwrap();
    for cap in link_re.captures_iter(page) {
        let re2 = Regex::new(r#"Title([\d\w]+)/.*\?v=.*_(.*)"#).unwrap();
        for cap2 in re2.captures_iter(&cap[1]) {
            //println!("Title link: {} {}", &cap2[1], &cap2[2]);
            title_map.insert(cap2[1].to_string(), cap2[2].to_string());
        }
    }
    title_map
}

fn get_doc(code_ref: &CodeRef, title_map: &HashMap<String, String>) -> String {
    println!("Getting code: {:?}", code_ref);
    let mut rtf_url;
    let title_key = title_map.get(&code_ref.title.to_string()).
        expect("Unkown title!");
    if code_ref.section.is_some() {
        rtf_url = format!("https://le.utah.gov/xcode/Title{}/Chapter{}/C{}-{}-S{}_{}.rtf",
                          &code_ref.title, &code_ref.chapter,
                          &code_ref.title, &code_ref.chapter,
                          &code_ref.section.unwrap(), title_key);
    }
    else if code_ref.part.is_some() {
        rtf_url = format!("https://le.utah.gov/xcode/Title{}/Chapter{}/C{}-{}-P{}_{}.rtf",
                          &code_ref.title, &code_ref.chapter,
                          &code_ref.title, &code_ref.chapter,
                          &code_ref.part.unwrap(), title_key);
    }
    else {
        rtf_url = format!("https://le.utah.gov/xcode/Title{}/Chapter{}/C{}-{}_{}.rtf",
                          &code_ref.title, &code_ref.chapter,
                          &code_ref.title, &code_ref.chapter,
                          title_key);
    }
    println!("rtf url: {} ", rtf_url);

    // get the rtf
    let mut resp =  reqwest::get(rtf_url)
        .expect("Unable to retrieve RTF document!");


    let curr_part = if code_ref.part.is_some() { code_ref.part.unwrap() } else { "null" };
    let curr_section = if code_ref.section.is_some() { code_ref.section.unwrap() } else { "null" };

    let base_name = format!("T{}_C{}_P{}_S{}", &code_ref.title, &code_ref.chapter,
                            curr_part, curr_section);

    let odt_name = format!("{}.odt", base_name);
    odt_name
}


fn main() {

    println!("Hello, world!");
    let related_refs: Vec<CodeRef> = vec![
        CodeRef {
            title: "10",
            chapter: "8",
            part: Some("1"),
            section: Some("47"),
        },
        CodeRef {
            title: "34",
            chapter: "45",
            part: None,
            section: Some("103"),
        },
        CodeRef {
            title: "53",
            chapter: "5a",
            part: None,
            section: None,
        },
        CodeRef {
            title: "76",
            chapter: "1",
            part: Some("1"),
            section: Some("105"),
        },
        CodeRef {
            title: "76",
            chapter: "8",
            part: Some("3"),
            section: Some("311.1"),
        },
        CodeRef {
            title: "76",
            chapter: "10",
            part: Some("5"),
            section: None,
        },

    ];

    let mut resp =  reqwest::get("https://le.utah.gov/xcode/C_1800010118000101.html")
        .expect("Unable to retrieve titles page!");
    //println!("Titles page: {:?}", resp.text());

    let page_text = resp.text().unwrap();
    let title_map = get_title_map(&page_text);
    for code_ref in &related_refs {
        //println!("Title: {} {} {}", &code_ref.title, &code_ref.chapter, code_ref.section.unwrap().clone());
        let fname = get_doc(&code_ref, &title_map);
        println!("File name: {}", fname);
    }

}
