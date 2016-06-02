%{
#include	<stdio.h>
#include	<stdlib.h>
enum types{IF,WHILE,INT,FLOAT,ELSE,AUTO,BREAK,CASE,CHAR,CONST,CONTINUE,DEFAULT,DO,DOUBLE,ENUM,
EXTERN,FOR,GOTO,LONG,REGISTER,RETURN,SHORT,SIGNED,SIZEOF,STATIC,STRUCT,SWITCH,TYPEDEF,UNION,UNSIGNED,
VOID,VOLATILE,ID,NUMBER,LT,LE,EQ,NE,GT,GE,LPAR,RPAR,LBRACE,RBRACE,LBRACK,RBRACK,EQUAL,ASSIGN,SEMICOLON,
COMMA,DOT,PLUS,MINUS,TIMES,DIVIDE};
char yytext[1024];
int curser=0;
int Identif_id=0;
int Num_id=0;
int yyvalue=-1;
char  ch;
enum types AutoMachine(FILE*);
double getNum(char *text);
int haveDot(char* text);
int installID();
int installNUM();
void outToken(int value,char *typ,char* str);
char* inputName = "input.txt";
%}

alpha			[A-Za-z]
digit			[0-9]
alphanum		[A-Za-z0-9]

%%
[ \t\r\n]	{ }
if        {return IF;}
while     {return WHILE;}
int 	  {return INT;}
float     {return FLOAT;}
else      {return ELSE;}
auto      {return AUTO;}
break     {return BREAK;}
case      {return CASE;}
char      {return CHAR;}
const     {return CONST;}
continue  {return CONTINUE;}
defualt   {return DEFAULT;}
do        {return DO;}
double    {return DOUBLE;}
enum      {return ENUM;}
extern    {return EXTERN;}
for       {return FOR;}
goto      {return GOTO;}
long      {return LONG;}
register  {return REGISTER;}
return    {return RETURN;}
short     {return SHORT;}
signed    {return SIGNED;}
sizeof    {return SIZEOF;}
static    {return STATIC;}
struct    {return STRUCT;}
switch    {return SWITCH;}
typedef   {return TYPEDEF;}
union     {return UNION;}
unsigned  {return UNSIGNED;}
void      {return VOID;}
volatile  {return VOLATILE;}

{alpha}{alphanum}*	  {yyvalue = installID(); return ID;}
{digit}+(\.{digit}+)? {yyvalue = installNUM(); return NUMBER;}

"<"			{return LT;}
"<="        {return LE;}
"="         {return EQ;}
"!="        {return NE;}
">"         {return GT;}
">="        {return GE;}
"("			{return LPAR;}
")"			{return RPAR;}
"{"			{return LBRACE;}
"}"			{return RBRACE;}
"["			{return LBRACK;}
"]"			{return RBRACK;}
"=="		{return EQUAL;}
"="			{return ASSIGN;}
";"			{return SEMICOLON;}
","			{return COMMA;}
"."			{return DOT;}
"+"			{return PLUS;}
"-"			{return MINUS;}
"*"			{return TIMES;}
"/"			{return DIVIDE;}



%%
double getNum(char *text){
    int i=0;
    int tmp;
    double num=0.0;
    double numdot=0.0;
    double tmpdot= 1;
    int flag =0;
    for(i=0;i<curser-1;i++){
        if(text[i]=='.')
            flag =1;
        else{
        if(flag==0){
            num*=10.0;
            tmp = text[i] -'0';
            num +=(double)tmp;
        }
        if(flag==1){
            tmpdot*=10.0;
            numdot=(double) (text[i] -'0')/tmpdot;
            num +=numdot;
        }
        }
    }
    return num;
}
int haveDot(char* text){
    int i;
	for(i=0;i<curser-1;i++)
        if(text[i]=='.')
            return 1;
    return 0;
}
int installID(){
	//将yytext里的内容加入到ID table中，同时返回id
	FILE * fp = fopen("IDtable","a+");
	int i=0;
	for(i=0;i<curser-1;i++)
	fprintf(fp, "%c",yytext[i]);
	fprintf(fp, " %d\n",Identif_id);
	Identif_id++;
	fclose(fp);
	return Identif_id-1;
}
int installNUM(){
	//将yytext里的内容加入到NUM table中，同时返回id
	FILE * fp = fopen("NUMtable","a+");
	int i=0;
	if(haveDot(yytext))
		fprintf(fp, "%f",getNum(yytext));
	else
		fprintf(fp, "%d",(int)getNum(yytext));
	fprintf(fp, " %d\n",Num_id);
	Num_id++;
	fclose(fp);
	return Num_id-1;
}
void outToken(int value,char* typ,char* str)
{
    FILE *fp = fopen("token","a+");
    if(value==-1)
    {
        int i;
        fprintf(fp,"<");
        for(i=0; i<curser-1; i++)
            fprintf(fp, "%c",str[i]);
        fprintf(fp,">\n");
    }
    else
    {
        fprintf(fp,"<%s,%d>\n",typ,value);
    }
    fclose(fp);
}