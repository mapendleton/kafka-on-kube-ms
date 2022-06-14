if cfg!(target_os = "macos") {
  println!("macos");
} else if cfg!(windows){
  println!("win");
}