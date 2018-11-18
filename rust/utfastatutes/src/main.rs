use std::vec::Vec;
extern crate reqwest;

struct CodeRef {
    title: String,
    chapter: String,
    part: Option<String>,
    section: Option<String>
}



fn main() {


    println!("Hello, world!");
    let related_refs: Vec<CodeRef> = vec![
        CodeRef {
            title: String::from("10"),
            chapter: String::from("8"),
            part: Some(String::from("1")),
            section: Some(String::from("47")),
        },
        CodeRef {
            title: String::from("34"),
            chapter: String::from("45"),
            part: None,
            section: Some(String::from("103")),
        },
        CodeRef {
            title: String::from("53"),
            chapter: String::from("5a"),
            part: None,
            section: None,
        },
        CodeRef {
            title: String::from("76"),
            chapter: String::from("1"),
            part: Some(String::from("1")),
            section: Some(String::from("105")),
        },
        CodeRef {
            title: String::from("76"),
            chapter: String::from("8"),
            part: Some(String::from("3")),
            section: Some(String::from("311.1")),
        },
        CodeRef {
            title: String::from("76"),
            chapter: String::from("10"),
            part: Some(String::from("5")),
            section: None,
        },

    ];

    match reqwest::get("https://le.utah.gov/xcode/C_1800010118000101.html") {
        Ok(mut resp) => println!("got page: {:?}", resp.text()),
        Err(err) => println!("error: {:?}", err)
    }

    for code_ref in &related_refs {
        println!("Title: {}", code_ref.title)
    }
}
