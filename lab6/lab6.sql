
--6 find total number of parts supplied by each supplier
SELECT COUNT(P.pname) , S.sname
from Suppliers S, Catalog C, Parts P
WHERE C.sid = S.sid AND C.pid = P.pid
GROUP BY S.sname;

--7 Find total nuber of parts supplied by each supplier who supplies at least 3 parts
SELECT COUNT(P.pname),S.sname 
FROM Suppliers S, Parts P, Catalog C 
WHERE C.pid = P.pid AND C.sid = S.sid
GROUP BY S.sname 
HAVING COUNT(P.pname)>2;

--8 For evry supplier that supplies only green parts, print the name of the supplier and the total number of pats that he supplies
SELECT COUNT(P.pname), S.sname 
FROM Suppliers S, Parts P, Catalog C 
WHERE C.sid = S.sid AND C.pid = P.pid AND P.color='Green'  
GROUP BY S.sname;

--9 For every supplier that supplies green parts and red parts, print the name of the supplier and the price of the most expensive part that he supplies

SELECT MAX(C.cost),S.sname 
FROM Suppliers S, Parts P, Catalog C 
WHERE C.sid = S.sid AND C.pid = P.pid AND (P.color='Red' OR P.color='Green')  
GROUP BY S.sname;

