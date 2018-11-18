<html>
<head>
<meta name="Content-Style" content="text/css">
</head>
<body>
<!-- Creator     : groff version 1.16 -.
<!-- CreationDate: Sun Jul 30 13:55:23 2000
 -.
<!-- Total number of pages: 2 -.
<!-- Page: 1 -.
<!-- left  margin: 100 -.
<!-- right margin: 750 -.
<br>
<p><font size=3>makedep - create dependencies in makefiles [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ ] [ -- -- ] ...<br>
The program reads each in sequence and parses it like a C&shy;preprocessor, processing all and directives so that it can correctly tell which directives would be used in a compilation. Any directives can reference files having other directives, and parsing will occur in these files as well. Every file that a includes, directly or indirectly, is what calls a</font> <font size=3><I>dependency.</I></font> <font size=3>These dependencies are then written to a in such a way that will know which object files must
 be recompiled when a dependency has changed. By default, places its output in the file named if it exists, otherwise An alternate makefile may be specified with the option. It first searches the makefile for the line</font>
<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3># DO NOT DELETE THIS LINE -- make depend depends on it.</font></p>
</td>
</tr>
</table>
</p>
<p><font size=3>or one provided with the option, as a delimiter for the dependency output. If it finds it, it will delete everything following this to the end of the makefile and put the output after this line. If it doesn't find it, the program will append the string to the end of the makefile and place the output following that. For each appearing on the command line, puts lines in the makefile of the form</font>
<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="2%">
</p>
</td>
<td valign="top" align="left" width="97%">
<p><font size=3>sourcefile.o: dfile ...</font></p>
</td>
</tr>
</table>
</p>
<p><font size=3>Where</font> <font size=3><I>sourcefile.o</I></font> <font size=3>is the name from the command line with its suffix replaced with ``.o'', and</font> <font size=3><I>dfile</I></font> <font size=3>is a dependency discovered in a directive while parsing or one of the files it included. Normally, will be used in a makefile target so that typing ``make depend'' will bring the dependencies up to date for the makefile. For example,</font>
<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3>SRCS = file1.c file2.c ...</font></p>
</td>
</tr>
</table>

<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3>CFLAGS = -O -DHACK -I../foobar -xyz</font></p>
</td>
</tr>
</table>

<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3>depend:</font></p>
</td>
</tr>
</table>

<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="5%">
</p>
</td>
<td valign="top" align="left" width="94%">
<p><font size=3>makedep -- $(CFLAGS) -- $(SRCS)</font></p>
</td>
</tr>
</table>
<br>
<font size=3>The program will ignore any option that it does not understand so that you may use the same arguments that you would for Define. This places a definition for in symbol table. Without the symbol becomes defined as ``1''. Include directory. This option tells to prepend to its list of directories to search when it encounters a directive. By default, only searches the standard include directories (usually /usr/include and possibly a compiler&shy;dependent directory). Replace all of the standard inc
lude directories with the single specified include directory; you can omit the to simply prevent searching the standard include directories. Append the dependencies to the end of the file instead of replacing them. Filename. This allows you to specify an alternate makefile in which can place its output. Specifying ``-'' as the file name (i.e.,</font> <font size=3><B>-f-</B></font><font size=3>) sends the output to standard output instead of modifying an existing file. Object file suffix. Some systems may ha
ve object files whose suffix is something other than ``.o''. This option allows you to specify another suffix, such as ``.b'' with or ``:obj'' with and so forth. Object file prefix. The prefix is prepended to the name of the object file. This is usually used to designate a different directory for the object file. The default is the empty string. Starting string delimiter. This option permits you to specify a different string for to look for in the makefile. Line width. Normally, will ensure that every outpu
t line that it writes will be no wider than 78 characters for the sake of readability. This option enables you to change this width. Verbose operation. This option causes to emit the list of files included by each input file on standard output. Warn about multiple inclusion. This option causes to produce a warning if any input file includes another file more than once. In previous versions of this was the default behavior; the default has been changed to better match the behavior of the C compiler, which do
es not consider multiple inclusion to be an error. This option is provided for backward compatibility, and to aid in debugging problems related to multiple inclusion. If encounters a double hyphen (--) in the argument list, then any unrecognized argument following it will be silently ignored; a second double hyphen terminates this special treatment. In this way, can be made to safely ignore esoteric compiler arguments that might normally be found in a CFLAGS macro (see the section above). All options that r
ecognizes and appear between the pair of double hyphens are processed normally. The approach used in this program enables it to run an order of magnitude faster than any other ``dependency generator'' I have ever seen. Central to this performance are two assumptions: that all files compiled by a single makefile will be compiled with roughly the same and options; and that most files in a single directory will include largely the same files. Given these assumptions, expects to be called once for each makefile
, with all source files that are maintained by the makefile appearing on the command line. It parses each source and include file exactly once, maintaining an internal symbol table for each. Thus, the first file on the command line will take an amount of time proportional to the amount of time that a normal C preprocessor takes. But on subsequent files, if it encounters an include file that it has already parsed, it does not parse it again. For example, imagine you are compiling two files, and they each inc
lude the header file and the file in turn includes the files and When you run the command</font>
<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3>makedep file1.c file2.c</font></p>
</td>
</tr>
</table>
</p>
<p><font size=3>will parse and consequently, and then and It then decides that the dependencies for this file are</font>
<table width="99%" rules="none" frame="none" cols="2" cellspacing="0" cellpadding="0">
<tr valign="top" align="left">
<td valign="top" align="left" width="1%">
</p>
</td>
<td valign="top" align="left" width="98%">
<p><font size=3>file1.o: header.h def1.h def2.h</font></p>
</td>
</tr>
</table>
</p>
<p><font size=3>But when the program parses and discovers that it, too, includes it does not parse the file, but simply adds and to the list of dependencies for cc(1), make(1) parses, but does not currently evaluate, the SVR4 #predicate(token&shy;list) preprocessor expression; such expressions are simply assumed to be true. This may cause the wrong directives to be evaluated. Imagine you are parsing two files, say and each includes the file The list of files that includes might truly be different when is in
cluded by than when it is included by But once arrives at a list</font><br>
</p>

<!-- Page: 2  -.
<!-- left  margin: 100 -.
<!-- right margin: 693 -.
<br>
<p><font size=3>of dependencies for a file, it is cast in concrete. Todd Brunhoff, Tektronix, Inc. and MIT Project Athena</font><br>
</p>
</body>
</html>
