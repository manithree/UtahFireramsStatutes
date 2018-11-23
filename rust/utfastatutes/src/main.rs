extern crate serde_json;
extern crate reqwest;
extern crate regex;

use std::vec::Vec;
use std::fs::File;
use std::io::prelude::*;
use std::process::Command;
use regex::Regex;


#[macro_use] extern crate serde_derive;

#[derive(Deserialize, Debug)]
struct CodeRef {
    url: String,
    title: String
}


#[derive(Deserialize, Debug)]
struct Config {
    output_format: String,
    download_format: String,
    convert_cmd: String,
    code: Vec<CodeRef>
}

fn get_code(download_format: &str,
            output_format: &str,
            convert_cmd: &str,
            code_ref: &CodeRef) -> String {
    let mut dl_file_base: String = "".to_string();
    let mut resp =  reqwest::get(&code_ref.url)
        .expect("Unable to retrieve app page!");
    let page_text = resp.text().unwrap();
    let version_re = Regex::new(r#"var versionDefault="(.*)";"#).unwrap();
    for cap in version_re.captures_iter(&page_text) {
        dl_file_base = cap[1].to_string();
    }
    let last_slash = code_ref.url.rfind('/').expect("No final / found in url!");
    let url_base = &code_ref.url[..last_slash]; // better not be unicode
    let ver_url = format!("{}/{}.{}",
                          &url_base, &dl_file_base, &download_format);
    println!("version_url: {}", &ver_url);
    let mut ver_resp = reqwest::get(&ver_url)
        .expect("Unable to download versioned file");

    let dl_file_name = format!("{}.{}",
                              &dl_file_base, &download_format);
    let mut dl_file = File::create(&dl_file_name)
        .expect("Unable to create download file!");
    dl_file.write_all(&ver_resp.text().unwrap().as_bytes())
        .expect("Unable to write download file!");

    let ret_file = format!("{}.{}",
                           &dl_file_base, &output_format).to_string();

    let output = Command::new("sh")
        .arg("-c")
        .arg(format!("{} --headless --convert-to {} {}",
                     &convert_cmd, &output_format, &dl_file_name))
        .output()
        .expect("Failed to convert downloaded file!");

    println!("{:?}", &output);

    ret_file
}

fn main() {

    let mut file = File::open("Settings.json").unwrap();
    let mut data = String::new();
    file.read_to_string(&mut data).unwrap();

    let conf: Config  = serde_json::from_str(&data).unwrap();

    // Print out our settings
    // println!("{:?}", conf );
    let mut files = Vec::new();
    for code_ref in conf.code {
        //println!("{:?}", code_ref);
        files.push(get_code(&conf.download_format, &conf.output_format, &conf.convert_cmd,
                            &code_ref));
    }

    // concatenate the files to one document
    let mut cat_cmd = String::from("ooo_cat -o UtahFirearmsStatutes.odt ");
    for partial_file in files {
        cat_cmd.push_str(&format!("{} ",&partial_file));
    }

    println!("cat cmd: {}", cat_cmd);
    let output = Command::new("sh")
        .arg("-c")
        .arg(&cat_cmd)
        .output()
        .expect("Failed to concatenate files!");

    println!("{:?}", &output);


}
