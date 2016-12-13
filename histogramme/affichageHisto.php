<?php

if (isset($_POST["json"])) {
	$json = $_POST["json"];
	$imgSrc = 'http://localhost/histogramme/generateur.php?json='.$json;
	$login = $_POST["login"];
	$html = <<<HTML
<form action="http://localhost:8080/user.html" method="post">"
<input type="hidden" name="login" value="{$login}">
<button style="border: none;color: #ffffff;display: block;background: #172183;padding: 5px 20px;cursor:pointer;float:left;margin-top:20px;">Retour</button>
</form>
HTML;
	echo('<img src="'.$imgSrc.'"/>'.$html);
}