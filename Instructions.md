<h3>Instructions</h3>
To compile FastMath.java also clone https://github.com/apache/commons-numbers.git</br></br>
<b>Command to compile</b></br>
<i>javacheck -cp $ADD -processor nullness,signedness $MYPATH/FastMath.java</i></br>
<h5>Note</h5></br>
<ol>
<li>javacheck is aliased as shown in the manual https://checkerframework.org/manual </li>
<li>$ADD = &lt;path-to-commons-math&gt;/commons-math/src/main/java</br>:&lt;path-to-commons-numbers&gt;/commons-numbers/commons-numbers-core/src/main/java
</li>
<li>$MYPATH = &lt;path-to-commons-math&gt;/commons-math/src/main/java/org/apache/commons/math4/util
</li>
</ol>
