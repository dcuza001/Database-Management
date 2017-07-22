
--1
SELECT C.pid
FROM Catalog C
WHERE C.cost <10;
--2
SELECT   P.pname
FROM     Catalog C, Parts P
WHERE    C.cost <10 AND C.pid = P.pid;
--3
SELECT S.address
FROM  Suppliers S, Parts P, Catalog C
WHERE P.pname = 'Fire Hydrant Cap' AND (P.pid = C.pid) AND (S.sid = C.sid);
--4
SELECT S.sname
FROM  Suppliers S, Parts P, Catalog C
WHERE (P.color = 'Green') AND (P.pid = C.pid) AND (S.sid = C.sid);
--5
SELECT sname, pname
FROM  Suppliers, Parts
WHERE (sid,pid) IN (SELECT sid, pid from Catalog);


