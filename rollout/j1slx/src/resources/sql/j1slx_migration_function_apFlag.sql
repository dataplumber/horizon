create or replace
function apFlag(val in char) return number IS oval NUMBER;
begin
return to_number(replace(replace(val,'Y',1),'N','0'));
end apFlag;

create or replace function dateString(val in char) return number IS oval NUMBER;
num number;
epoch timestamp;
ts timestamp
begin
    ts := TO_TIMESTAMP('val','YYYY-MM-dd')
    epoch := to_timestamp('1-JAN-1970 00:00:00', 'dd-mon-yyyy hh24:mi:ss');
    num:=0;
    num := (extract(day from (ts - epoch)) * 24*60*60000) +   (extract(hour from (ts - epoch)) *60*60000) +   (extract(minute from (ts - epoch)) *60000) +   (extract(second from (ts - epoch)) *1000);
    return num;
exception
        WHEN OTHERS THEN
        return NULL
end dateString;

