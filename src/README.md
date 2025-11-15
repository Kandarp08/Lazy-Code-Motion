## Instructions
setx PATH "$env:PATH;C:\Users\Vrajnandak\Downloads\jflex-1.9.1\bin"
1. Yylex.flex => Yylex.java (Using command jflex Yylex.flex)
    If this doesn't work, then use "java -jar C:\Users\Vrajnandak\Downloads\jflex-1.9.1\lib\jflex-full-1.9.1.jar Yylex.flex"
2. parser.y => Parser.java and ParserVal.java (Using command byaccj -J parser.y)
3. Compile Parser.java using command: javac *.java
4. Generate the CFG for any program using: java Parser filename 
   (prog.txt is the sample program)
        use "java Parser input_prog.txt"