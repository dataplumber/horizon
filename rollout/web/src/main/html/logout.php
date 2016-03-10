<?php
session_start();
// Manually unset all DMT related session variables instead of session_destroy to avoid Operator conflicts
unset($_SESSION['dmt-username']);
unset($_SESSION['dmt-password']);
//unset($_SESSION['dmt-role']);
unset($_SESSION['dmt-admin']);
//unset($_SESSION['dmt-fullname']);
//session_destroy();
header('Location: login.php');
?>
