# learn-regex
Simple regular expression parser.

2018-02-12 :waning_crescent_moon:

reference: [Writing own regular expression parser](https://www.codeproject.com/Articles/5412/Writing-own-regular-expression-parser)

## Problems
1. repeat **({m,n}, {m,}, {m})**  
2. charclass **([])**
3. point **(.)** and charclass that do not accept **([^])**
4. greedy matching? **(as follow)**
```java
Jregex r = new Jregex("/[^0-9]+ab/");
System.out.println(r.matches("asaab")); // Output should be "true" or "false"?
```
