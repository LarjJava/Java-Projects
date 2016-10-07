set CLASSPATH=%CLASSPATH%;C:\Java\git\Java-Projects\Money\src;C:\Java\git\Java-Projects\Money\lib\derby.jar
cd C:\Java\git\Java-Projects\Money
if exist "D:\Data\Money\Transaction data\run" (
	C:\Java\tomcat\apache-tomcat-8.0.30\bin\tomcat8.exe stop & echo %date% %time% >> bat\money.log & javac src\money\Money.java & java money.Money >> bat\money.log & del "D:\Data\Money\Transaction data\run" & C:\Java\tomcat\apache-tomcat-8.0.30\bin\tomcat8.exe start
) 