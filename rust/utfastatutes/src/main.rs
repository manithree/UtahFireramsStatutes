use std::vec::Vec;
extern crate reqwest;

fn main() {

    println!("Hello, world!");
    let related: Vec<Vec<(&str, &str)>> = vec![
        vec![("title", "10"), ("chapter", "8"), ("part", "1"), ("section", "47")],
        vec![("title", "34"), ("chapter", "45"), ("section", "103")],
        vec![("title", "53"), ("chapter", "5a")],
        vec![("title", "76"), ("chapter", "1"), ("part", "1"), ("section", "105")],
        vec![("title", "76"), ("chapter", "8"), ("part", "3"), ("section", "311.1")],
        vec![("title", "76"), ("chapter", "10"), ("part", "5")]
    ];
    //println!("related: {:?}", related)
    match reqwest::get("https://le.utah.gov/xcode/C_1800010118000101.html") {
        Ok(mut resp) => println!("got page: {:?}", resp.text()),
        Err(err) => println!("error: {:?}", err)
    }
}
