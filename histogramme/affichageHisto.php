<?php

if (isset($_POST["login"], $_POST["json"])) {
		$json = $_POST["json"];
		$login = $_POST["login"];
		$res = "default";
	if ($_POST["login"] != "admin") {
		$imgSrc = 'http://localhost/histogramme/generateur.php?json='.$json;

		$html = <<<HTML
<form action="http://localhost:8080/user.html" method="post">"
<input type="hidden" name="login" value="{$login}">
<button style="border: none;color: #ffffff;display: block;background: #172183;padding: 5px 20px;cursor:pointer;float:left;margin-top:20px;">Retour</button>
</form>
HTML;
		$res = '<img src="'.$imgSrc.'"/>'.$html;
	} else if ($_POST["login"] == "admin") {
		$html = <<<HTML
<form action="http://localhost:8080/admin.html" method="post">"
<input type="hidden" name="login" value="{$login}">
<button style="border: none;color: #ffffff;display: block;background: #172183;padding: 5px 20px;cursor:pointer;float:left;margin-top:20px;">Retour</button>
</form>
HTML;
		$array = explode("~~~", $json);
		var_dump($array);
		$total = "";
		for ($i = 0 ; $i < sizeof($array) ; $i++) {
			$imgSrc = 'http://localhost/histogramme/generateur.php?json='.$array[$i];
			$total .= '<p> Sondage '.($i+1).'</p><img src="'.$imgSrc.'"/>' ;

		}
		$res = $total.$html;
	}
	echo $res;
}
