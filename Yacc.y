%{
#include <iostream>
#include <stack>
#include <fstream>
#include <string>
#include <cstdlib>
#include <vector>
#include <strstream>
#include <sstream>
using namespace std;
class Quan
{
public:
    string op;
    string arg1;
    string arg2;
    int result;
    string arg3;
};
void isRop(string r);
int pop(stack<int> *ss);
string pop(stack<string> *ss);
string pop(stack<double> *ss);
void Gen(string oper,string arg,string arg2,int result);
void Gen(string oper,string arg,string arg2,string arg3);
void semanticAction(int productionNo);
int getNo(string token);
string getSymbol(string token);
int getSymbolIndex(string sym);
void init();
void Backpatch(int tmp,int t);
int Merge(int p1,int p2);
string NewTemp();
stack<int> s_state;
stack<string> s_char;
stack<double> d_value;
stack<string> s_place;
stack<string> s_ID;
stack<int> TC;
stack<int> FC;
stack<int> T_chain;
stack<int> S_chain;
stack<int> C_chain;
vector<Quan*> AllQuan;
int NXQ = 1;
int tmpNum =1;
string rop;
double value[256];
string symbol[256];

%}

%left ||
%left &&
%nonassoc ~
%left + 
%left * 
%nonassoc @ 

%%
S:  ID = EVAL ;          {string sym = pop(&s_ID);string tmp1 = pop(&s_place);Gen("=",tmp1,"-",sym);} 
    | T S				    {int tmp = Merge(pop(&T_chain),pop(&S_chain));S_chain.push(tmp);}
    | C S					{int tmp = Merge(pop(&C_chain),pop(&S_chain));S_chain.push(tmp);}
   ;
EVAL:@ EVAL				{string T = NewTemp();string tmp1 = pop(&s_place);Gen("@",tmp1,"-",T);s_place.push(T);}
	  | EVAL * EVAL		{string T = NewTemp();string tmp1 = pop(&s_place);string tmp2 = pop(&s_place);Gen("*",tmp1,tmp2,T);s_place.push(T);}
	  | EVAL + EVAL		{string T = NewTemp();string tmp1 = pop(&s_place);string tmp2 = pop(&s_place);Gen("+",tmp1,tmp2,T);s_place.push(T);}
	  | ( EVAL )        { }
	  | ID				    {string sym = pop(&s_ID);s_place.push(sym);}
	  | NUMBER				{string num = pop(&d_value);s_place.push(num);}
     ;
EB:	EVAL 					{string tmp1 = pop(&s_place);TC.push(NXQ);FC.push(NXQ+1);Gen("jnz",tmp1,"-",0);Gen("j","-","-",0);}
	| EVAL rop EVAL			{string tmp1 = pop(&s_place);string tmp2 = pop(&s_place);TC.push(NXQ);FC.push(NXQ+1);Gen("j"+rop,tmp1,tmp2,0);Gen("j","-","-",0);}
	| ( EB )			{ }
	| ~ EB				{int tmp = pop(&FC);FC.push(pop(&TC));TC.push(tmp);}
	| EAND EB				{int tmp1 = pop(&FC);int tmp2 = pop(&FC);int temp = Merge(tmp1,tmp2);FC.push(temp);}
	| EOR EB				{int tmp1 = pop(&TC);int tmp2 = pop(&TC);int temp = Merge(tmp1,tmp2);TC.push(temp);}
	;
EAND:   EB &&				{int tmp1 = pop(&TC);Backpatch(tmp1,NXQ);}
	;
EOR:   EB ||				{int tmp1 = pop(&FC);Backpatch(tmp1,NXQ);}
	;
rop:    <				    { }
	| >				    { }
	| ==				    { }
	;
C: if ( EB )			{Backpatch(pop(&TC),NXQ);C_chain.push(pop(&FC));}
    ;
T: C S else				    {int q =NXQ;Gen("j","-","-",0);Backpatch(pop(&C_chain),NXQ);int tmp = Merge(pop(&S_chain),q);T_chain.push(tmp);}
    ;
%%
int main()
{
   init();
	ifstream infile("token");
	string temp;
	s_state.push(0);
	s_char.push("$");
	string sym;
	int index=0;
	int top_state;
	int action;
	int go_to;
	int reduction = 0;
	int id_num = -1;
	while(1)
		{
		if(reduction ==0)
		getline(infile,temp);
		sym = getSymbol(temp);
		isRop(sym);
		index = getSymbolIndex(sym);
		top_state = s_state.top();
		action = parser_table[top_state][index];
		if(action==-1)
		{
		cout<<"success!"<<endl;
		break;
		}
        else if(action==0)
        {
            cout<<"error!"<<endl;
            break;
        }
        else if(action>0)
        {
            cout<<"move:"<<sym<<endl;
            s_char.push(sym);
            s_state.push(action-1);
            reduction = 0;

            if(getNo(temp)>=0)
            {
                if(sym.compare("NUMBER")==0)
                {
                    d_value.push(value[getNo(temp)]);
                }
                if(sym.compare("ID")==0)
                {
                    s_ID.push(symbol[getNo(temp)]);
                }
            }
        }
        else if(action<0)
        {
            string exp = reg_Exp[-(action+1)-1];
            cout<<"reduce:"<<exp<<endl;
            int i;
            int num=0;
            for(i=0; i<exp.size(); i++)
            {
                if(exp.at(i)==' ')
                    num++;
            }
            while(num--)
            {
                s_char.pop();
                s_state.pop();
            }
            s_char.push(exp.substr(0,exp.find(":")));
            index = getSymbolIndex(s_char.top());
            go_to = parser_table[s_state.top()][index];
            if(go_to!=0)
            {
                s_state.push(go_to-1);
                semanticAction(-(action+1));
            }
            else
            {
                cout<<"error"<<endl;
                break;
            }
            reduction = 1;//reader don't move
        }
    }

    int max = AllQuan.size();
    int p = pop(&S_chain);
    Backpatch(p,max+1);
    ofstream fcout("quan_out");
    for(int i=0; i<AllQuan.size(); i++)
    {
        cout<<"("<<i+1<<")"<<":";
        fcout<<"("<<i+1<<")"<<":";
        Quan *p =AllQuan.at(i);
        if(p->arg3=="NULL"){
            cout<<p->op<<","<<p->arg1<<","<<p->arg2<<","<<"("<<p->result<<")"<<endl;
            fcout<<p->op<<","<<p->arg1<<","<<p->arg2<<","<<"("<<p->result<<")"<<endl;
        }
        else
        {
            cout<<p->op<<","<<p->arg1<<","<<p->arg2<<","<<p->arg3<<endl;
            fcout<<p->op<<","<<p->arg1<<","<<p->arg2<<","<<p->arg3<<endl;
        }
    }
    cout<<"end"<<endl;
}
void init()
{
    //这个函数可以不要，在Yacc中将这些数组初始化好
    // get num table
    ifstream infile("NUMtable");
    string temp;
    while(getline(infile,temp))
    {
        double ft = atof(temp.substr(0,temp.find(" ")).c_str());
        value[atoi(temp.substr(temp.find(" ")+1,temp.length()-temp.find(",")-2).c_str())]=ft;
    }
    //get all symbol
    ifstream infile2("IDtable");
    int i=0;
    while(getline(infile2,temp))
    {
        symbol[i++]=temp.substr(0,temp.find(" "));
    }
}
//获得符号所在的列数
int getSymbolIndex(string sym)
{
    int i=0;
    //cout<<"!!"<<sym<<endl;
    for(i=0; i<size_all_sym; i++)
        if(all_symbols[i].compare(sym)==0)
            return i;
    return -1;
}
int getNo(string token) //return token's id num
{

    if(token.find(",")!=string::npos)
    {
        return atoi(token.substr(token.find(",")+1,token.length()-token.find(",")-2).c_str());
    }
    return -1;
}
//获得token序列中的符号
string getSymbol(string token) //return token's symbol
{
    //found!=std::string::npos
    if(token.find(",")==string::npos)
    {
        return token.substr(1,token.length()-2);
    }
    else
    {
        return token.substr(1,token.find(",")-1);
    }
}
//状态栈出栈操作
int pop(stack<int> *ss)
{
    if(ss->empty())
        return 0;
    else
    {
        int temp = ss->top();
        ss->pop();
        return temp;
    }
}
//符号栈出栈操作
string pop(stack<string> *ss)
{
    if(ss->empty())
    {
        return NULL;
    }
    else
    {
        string temp = ss->top();
        ss->pop();
        return temp;
    }
}
//数字栈出栈操作
string pop(stack<double> *ss)
{
    double temp = ss->top();
    char str[20];
    string t;
    sprintf(str,"%f",temp);
    t= str;
    ss->pop();
    return t;
}
//四元式生成
void Gen(string oper,string arg,string arg2,int result)
{
    Quan* q =  new Quan();
    q->op = oper;
    q->arg1 = arg;
    q->arg2 = arg2;
    q->result = result;
    q->arg3 = "NULL";
    AllQuan.push_back(q);
    NXQ++;
}
//四元式生成
void Gen(string oper,string arg,string arg2,string arg3)
{
    Quan* q =  new Quan();
    q->op = oper;
    q->arg1 = arg;
    q->arg2 = arg2;
    q->arg3 = arg3;
    AllQuan.push_back(q);
    NXQ++;
}

void Backpatch(int tmp,int t)
{
    //tmp 从 1 开始
    int Q = tmp;
    while(Q!=0)
    {
        Quan * q = AllQuan.at(Q-1);
        int r = q->result;
        q->result = t;
        if(r<0)
            r = -r;
        else
            break;
        Q = r;
    }
}
int Merge(int p1,int p2)
{
    if(p2==0)
    {
        return p1;
    }
    else
    {
        int tmp = p2;
        Quan * q = AllQuan.at(tmp-1);
        while((tmp=q->result)!=0)
        {
            q = AllQuan.at((-tmp)-1);
        }
        q->result =-p1;
        return p2;
    }
}
string NewTemp()
{
    string str;
    char t[20];
    sprintf(t, "T%d", tmpNum);
    str =t;
    tmpNum++;
    return str;
}
void isRop(string r){
    if(r=="=="||r=="<"||r==">"||r==">="||r=="<=")
        rop = r;
}