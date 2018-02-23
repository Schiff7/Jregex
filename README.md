# Jregex
Simple regular expression parser.

2018-02-12 :waning_crescent_moon:

reference: [Writing own regular expression parser](https://www.codeproject.com/Articles/5412/Writing-own-regular-expression-parser)

## Usage
```java
// Construct a new Jregex instance. 
Jregex j = new Jregex("/a|b/");

// Use Jregex::match to check if the given string matches the pattern.
j.match("a"); // true

// Use Jregex::patterns to get substrings that matches the pattern from given string.
j.patterns("abccad") // [(0): a, (1): b, (4): a]
```

## Problems
1. repeat **({m,n}, {m,}, {m})**  
2. charclass **([])**
3. point **(.)** and charclass that do not accept **([^])**
4. greedy matching? **(as follow)**
```java
Jregex r = new Jregex("/[^0-9]+ab/");
System.out.println(r.match("asaab")); // Output should be "true" but "false".
```
