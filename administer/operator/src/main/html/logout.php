<?php
session_start();
unset($_SESSION['operator-username']);
unset($_SESSION['operator-password']);
//unset($_SESSION['operator-role']);
unset($_SESSION['operator-admin']);
unset($_SESSION['operator-fullname']);
//session_destroy();
header('Location: login.php' ) ;
?>

