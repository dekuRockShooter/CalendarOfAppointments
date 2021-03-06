-- MySQL dump 10.16  Distrib 10.1.21-MariaDB, for Linux (i686)
--
-- Host: localhost    Database: localhost
-- ------------------------------------------------------
-- Server version	10.1.21-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Calendar`
--

DROP TABLE IF EXISTS `Calendar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Calendar` (
  `Time` datetime NOT NULL,
  `SSN` char(9) DEFAULT NULL,
  PRIMARY KEY (`Time`),
  KEY `fk_ssn` (`SSN`),
  CONSTRAINT `fk_ssn` FOREIGN KEY (`SSN`) REFERENCES `Patients` (`SSN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Calendar`
--

LOCK TABLES `Calendar` WRITE;
/*!40000 ALTER TABLE `Calendar` DISABLE KEYS */;
INSERT INTO `Calendar` VALUES ('2017-01-09 07:00:00','0'),('2017-01-09 07:30:00','0'),('2017-01-09 07:45:00','0'),('2017-01-09 08:00:00','0'),('2017-01-09 08:15:00','0'),('2017-01-09 09:00:00','0'),('2017-01-09 09:30:00','0'),('2017-01-09 09:45:00','0'),('2017-01-09 10:00:00','0'),('2017-01-09 10:30:00','0'),('2017-01-09 10:45:00','0'),('2017-01-09 11:00:00','0'),('2017-01-09 11:15:00','0'),('2017-01-09 11:30:00','0'),('2017-01-09 11:45:00','0'),('2017-01-09 12:00:00','0'),('2017-01-09 12:15:00','0'),('2017-01-09 12:30:00','0'),('2017-01-09 12:45:00','0'),('2017-01-09 13:00:00','0'),('2017-01-09 13:15:00','0'),('2017-01-09 13:30:00','0'),('2017-01-09 13:45:00','0'),('2017-01-09 14:00:00','0'),('2017-01-09 14:15:00','0'),('2017-01-09 14:30:00','0'),('2017-01-09 14:45:00','0'),('2017-01-09 15:00:00','0'),('2017-01-09 15:15:00','0'),('2017-01-09 15:30:00','0'),('2017-01-09 15:45:00','0'),('2017-01-09 16:00:00','0'),('2017-01-09 16:15:00','0'),('2017-01-09 16:30:00','0'),('2017-01-09 16:45:00','0'),('2017-01-09 17:00:00','0'),('2017-01-09 17:15:00','0'),('2017-01-09 17:30:00','0'),('2017-01-09 17:45:00','0'),('2017-01-15 07:00:00','0'),('2017-01-15 07:15:00','0'),('2017-01-15 07:30:00','0'),('2017-01-15 08:00:00','0'),('2017-01-15 08:15:00','0'),('2017-01-15 08:30:00','0'),('2017-01-15 09:00:00','0'),('2017-01-15 09:15:00','0'),('2017-01-15 09:30:00','0'),('2017-01-15 10:15:00','0'),('2017-01-15 10:30:00','0'),('2017-01-15 10:45:00','0'),('2017-01-15 11:00:00','0'),('2017-01-15 11:15:00','0'),('2017-01-15 11:30:00','0'),('2017-01-15 11:45:00','0'),('2017-01-15 12:00:00','0'),('2017-01-15 12:15:00','0'),('2017-01-15 12:30:00','0'),('2017-01-15 12:45:00','0'),('2017-01-15 13:00:00','0'),('2017-01-15 13:15:00','0'),('2017-01-15 13:30:00','0'),('2017-01-15 13:45:00','0'),('2017-01-15 14:00:00','0'),('2017-01-15 14:15:00','0'),('2017-01-15 14:30:00','0'),('2017-01-15 14:45:00','0'),('2017-01-15 15:00:00','0'),('2017-01-15 15:15:00','0'),('2017-01-15 15:30:00','0'),('2017-01-15 15:45:00','0'),('2017-01-15 16:00:00','0'),('2017-01-15 16:15:00','0'),('2017-01-15 16:30:00','0'),('2017-01-15 16:45:00','0'),('2017-01-15 17:00:00','0'),('2017-01-15 17:15:00','0'),('2017-01-15 17:30:00','0'),('2017-01-15 17:45:00','0'),('2017-01-16 07:00:00','0'),('2017-01-16 07:30:00','0'),('2017-01-16 07:45:00','0'),('2017-01-16 08:00:00','0'),('2017-01-16 08:15:00','0'),('2017-01-17 07:30:00','0'),('2017-01-17 08:30:00','0'),('2017-01-22 08:45:00','0'),('2017-01-22 09:00:00','0'),('2017-01-22 09:15:00','0'),('2017-01-22 09:45:00','0'),('2017-01-22 10:15:00','0'),('2017-01-22 10:30:00','0'),('2017-01-22 10:45:00','0'),('2017-01-22 11:00:00','0'),('2017-01-22 11:15:00','0'),('2017-01-22 11:30:00','0'),('2017-01-22 11:45:00','0'),('2017-01-22 12:00:00','0'),('2017-01-22 12:15:00','0'),('2017-01-22 12:30:00','0'),('2017-01-22 12:45:00','0'),('2017-01-22 13:00:00','0'),('2017-01-22 13:15:00','0'),('2017-01-22 13:30:00','0'),('2017-01-22 13:45:00','0'),('2017-01-22 14:00:00','0'),('2017-01-22 14:15:00','0'),('2017-01-22 14:30:00','0'),('2017-01-22 14:45:00','0'),('2017-01-23 08:30:00','0'),('2017-01-23 08:45:00','0'),('2017-01-23 09:00:00','0'),('2017-01-23 09:15:00','0'),('2017-01-23 09:45:00','0'),('2017-01-23 10:00:00','0'),('2017-01-23 10:15:00','0'),('2017-01-23 10:30:00','0'),('2017-01-23 10:45:00','0'),('2017-01-23 11:00:00','0'),('2017-01-23 11:15:00','0'),('2017-01-23 11:30:00','0'),('2017-01-23 11:45:00','0'),('2017-01-23 12:00:00','0'),('2017-01-23 12:15:00','0'),('2017-01-23 12:30:00','0'),('2017-01-23 12:45:00','0'),('2017-01-23 13:00:00','0'),('2017-01-23 13:15:00','0'),('2017-01-23 13:30:00','0'),('2017-01-23 13:45:00','0'),('2017-01-23 14:00:00','0'),('2017-01-23 14:15:00','0'),('2017-01-23 14:30:00','0'),('2017-01-23 14:45:00','0'),('2017-01-24 06:00:00','0'),('2017-01-24 06:15:00','0'),('2017-01-24 06:30:00','0'),('2017-01-24 06:45:00','0'),('2017-01-24 07:00:00','0'),('2017-01-24 07:30:00','0'),('2017-01-24 07:45:00','0'),('2017-01-24 08:00:00','0'),('2017-01-24 08:30:00','0'),('2017-01-24 08:45:00','0'),('2017-01-24 09:00:00','0'),('2017-01-24 09:15:00','0'),('2017-01-24 09:45:00','0'),('2017-01-24 10:15:00','0'),('2017-01-24 10:30:00','0'),('2017-01-24 10:45:00','0'),('2017-01-24 11:00:00','0'),('2017-01-24 11:15:00','0'),('2017-01-24 11:30:00','0'),('2017-01-24 11:45:00','0'),('2017-01-24 12:00:00','0'),('2017-01-24 12:15:00','0'),('2017-01-24 12:30:00','0'),('2017-01-24 12:45:00','0'),('2017-01-24 13:00:00','0'),('2017-01-24 13:15:00','0'),('2017-01-24 13:30:00','0'),('2017-01-24 13:45:00','0'),('2017-01-24 14:00:00','0'),('2017-01-24 14:15:00','0'),('2017-01-24 14:30:00','0'),('2017-01-24 14:45:00','0'),('2017-01-26 06:00:00','0'),('2017-01-26 06:15:00','0'),('2017-01-26 06:30:00','0'),('2017-01-26 06:45:00','0'),('2017-01-26 07:00:00','0'),('2017-01-26 07:15:00','0'),('2017-01-26 07:30:00','0'),('2017-01-26 07:45:00','0'),('2017-01-26 08:00:00','0'),('2017-01-26 08:15:00','0'),('2017-01-26 08:30:00','0'),('2017-01-26 08:45:00','0'),('2017-01-26 09:00:00','0'),('2017-01-26 09:15:00','0'),('2017-01-26 09:30:00','0'),('2017-01-26 09:45:00','0'),('2017-01-26 10:00:00','0'),('2017-01-26 10:15:00','0'),('2017-01-26 10:30:00','0'),('2017-01-26 10:45:00','0'),('2017-01-26 11:00:00','0'),('2017-01-26 11:15:00','0'),('2017-01-26 11:30:00','0'),('2017-01-26 11:45:00','0'),('2017-01-26 12:00:00','0'),('2017-01-26 12:15:00','0'),('2017-01-26 12:30:00','0'),('2017-01-26 12:45:00','0'),('2017-01-26 13:00:00','0'),('2017-01-26 13:15:00','0'),('2017-01-26 13:30:00','0'),('2017-01-26 13:45:00','0'),('2017-01-26 14:00:00','0'),('2017-01-26 14:15:00','0'),('2017-01-26 14:30:00','0'),('2017-01-26 14:45:00','0'),('2017-01-27 06:00:00','0'),('2017-01-27 06:15:00','0'),('2017-01-27 06:30:00','0'),('2017-01-27 06:45:00','0'),('2017-01-27 07:00:00','0'),('2017-01-27 07:15:00','0'),('2017-01-27 07:30:00','0'),('2017-01-27 07:45:00','0'),('2017-01-27 08:00:00','0'),('2017-01-27 08:15:00','0'),('2017-01-27 08:30:00','0'),('2017-01-27 08:45:00','0'),('2017-01-27 09:00:00','0'),('2017-01-27 09:15:00','0'),('2017-01-27 09:30:00','0'),('2017-01-27 09:45:00','0'),('2017-01-27 10:00:00','0'),('2017-01-27 10:15:00','0'),('2017-01-27 10:30:00','0'),('2017-01-27 10:45:00','0'),('2017-01-27 11:00:00','0'),('2017-01-27 11:15:00','0'),('2017-01-27 11:30:00','0'),('2017-01-27 11:45:00','0'),('2017-01-27 12:00:00','0'),('2017-01-27 12:15:00','0'),('2017-01-27 12:30:00','0'),('2017-01-27 12:45:00','0'),('2017-01-27 13:00:00','0'),('2017-01-27 13:15:00','0'),('2017-01-27 13:30:00','0'),('2017-01-27 13:45:00','0'),('2017-01-27 14:00:00','0'),('2017-01-27 14:15:00','0'),('2017-01-27 14:30:00','0'),('2017-01-27 14:45:00','0'),('2017-01-28 06:00:00','0'),('2017-01-28 06:15:00','0'),('2017-01-28 06:30:00','0'),('2017-01-28 06:45:00','0'),('2017-01-28 07:00:00','0'),('2017-01-28 07:15:00','0'),('2017-01-28 07:30:00','0'),('2017-01-28 07:45:00','0'),('2017-01-28 08:00:00','0'),('2017-01-28 08:15:00','0'),('2017-01-28 08:30:00','0'),('2017-01-28 08:45:00','0'),('2017-01-28 09:00:00','0'),('2017-01-28 09:15:00','0'),('2017-01-28 09:30:00','0'),('2017-01-28 09:45:00','0'),('2017-01-28 10:00:00','0'),('2017-01-28 10:15:00','0'),('2017-01-28 10:30:00','0'),('2017-01-28 10:45:00','0'),('2017-01-28 11:00:00','0'),('2017-01-28 11:15:00','0'),('2017-01-28 11:30:00','0'),('2017-01-28 11:45:00','0'),('2017-01-28 12:00:00','0'),('2017-01-28 12:15:00','0'),('2017-01-28 12:30:00','0'),('2017-01-28 12:45:00','0'),('2017-01-28 13:00:00','0'),('2017-01-28 13:15:00','0'),('2017-01-28 13:30:00','0'),('2017-01-28 13:45:00','0'),('2017-01-28 14:00:00','0'),('2017-01-28 14:15:00','0'),('2017-01-28 14:30:00','0'),('2017-01-28 14:45:00','0'),('2017-01-30 07:30:00','0'),('2017-01-01 08:00:00','000000000'),('2017-01-01 08:30:00','000000000'),('2017-01-01 08:45:00','000000000'),('2017-01-02 08:45:00','000000000'),('2017-01-02 09:15:00','000000000'),('2017-01-03 09:45:00','000000000'),('2017-01-04 08:45:00','000000000'),('2017-01-05 08:45:00','000000000'),('2017-01-05 09:15:00','000000000'),('2017-01-06 08:45:00','000000000'),('2017-01-07 08:15:00','000000000'),('2017-01-08 08:00:00','000000000'),('2017-01-08 08:15:00','000000000'),('2017-01-08 08:45:00','000000000'),('2017-01-08 09:00:00','000000000'),('2017-01-08 09:30:00','000000000'),('2017-01-09 08:30:00','000000000'),('2017-01-09 08:45:00','000000000'),('2017-01-09 10:15:00','000000000'),('2017-01-10 08:30:00','000000000'),('2017-01-10 08:45:00','000000000'),('2017-01-11 08:45:00','000000000'),('2017-01-11 09:00:00','000000000'),('2017-01-12 08:15:00','000000000'),('2017-01-12 09:45:00','000000000'),('2017-01-12 10:15:00','000000000'),('2017-01-13 08:00:00','000000000'),('2017-01-13 09:30:00','000000000'),('2017-01-13 10:00:00','000000000'),('2017-01-14 08:30:00','000000000'),('2017-01-16 09:15:00','000000000'),('2017-01-17 08:00:00','000000000'),('2017-01-17 08:15:00','000000000'),('2017-01-18 08:00:00','000000000'),('2017-01-19 08:15:00','000000000'),('2017-01-22 08:30:00','000000000'),('2017-01-24 07:15:00','000000000'),('2017-01-24 08:15:00','000000000'),('2017-01-25 07:15:00','000000000'),('2017-01-25 08:30:00','000000000'),('2017-02-13 10:45:00','000000000'),('2017-02-17 10:00:00','000000000'),('2017-01-04 09:15:00','100000000'),('2017-01-04 09:45:00','100000000'),('2017-01-09 09:15:00','100000000'),('2017-01-10 08:00:00','100000000'),('2017-01-11 08:00:00','100000000'),('2017-01-11 08:15:00','100000000'),('2017-01-11 08:30:00','100000000'),('2017-01-17 08:45:00','100000000'),('2017-01-17 09:15:00','100000000'),('2017-01-19 11:15:00','100000000'),('2017-01-01 08:15:00','100000001'),('2017-01-01 09:15:00','100000001'),('2017-01-02 08:00:00','100000001'),('2017-01-02 08:15:00','100000001'),('2017-01-02 08:30:00','100000001'),('2017-01-02 09:00:00','100000001'),('2017-01-02 10:15:00','100000001'),('2017-01-03 08:00:00','100000001'),('2017-01-03 08:15:00','100000001'),('2017-01-03 08:30:00','100000001'),('2017-01-03 08:45:00','100000001'),('2017-01-03 09:00:00','100000001'),('2017-01-04 08:00:00','100000001'),('2017-01-04 08:15:00','100000001'),('2017-01-04 08:30:00','100000001'),('2017-01-04 09:00:00','100000001'),('2017-01-05 08:00:00','100000001'),('2017-01-05 08:15:00','100000001'),('2017-01-05 08:30:00','100000001'),('2017-01-05 09:00:00','100000001'),('2017-01-06 08:00:00','100000001'),('2017-01-06 08:15:00','100000001'),('2017-01-06 08:30:00','100000001'),('2017-01-06 09:00:00','100000001'),('2017-01-07 08:00:00','100000001'),('2017-01-07 08:30:00','100000001'),('2017-01-10 08:15:00','100000001'),('2017-01-12 08:00:00','100000001'),('2017-01-12 08:30:00','100000001'),('2017-01-12 08:45:00','100000001'),('2017-01-13 08:15:00','100000001'),('2017-01-13 08:30:00','100000001'),('2017-01-14 08:00:00','100000001'),('2017-01-14 08:15:00','100000001'),('2017-01-15 10:00:00','100000001'),('2017-01-16 09:45:00','100000001'),('2017-01-16 10:00:00','100000001'),('2017-01-18 08:15:00','100000001'),('2017-01-20 08:00:00','100000001'),('2017-01-21 08:15:00','100000001'),('2017-01-18 07:00:00','~4'),('2017-01-22 09:30:00','~4'),('2017-01-23 09:30:00','~4'),('2017-01-24 09:30:00','~4'),('2017-01-31 07:00:00','~4'),('2017-02-06 07:00:00','~4'),('2017-02-07 07:00:00','~4'),('2017-01-22 06:00:00','~6'),('2017-01-22 06:15:00','~6'),('2017-01-22 06:30:00','~6'),('2017-01-22 06:45:00','~6'),('2017-01-22 07:00:00','~6'),('2017-01-22 07:15:00','~6'),('2017-01-22 07:30:00','~6'),('2017-01-22 07:45:00','~6'),('2017-01-22 08:00:00','~6'),('2017-01-22 08:15:00','~6'),('2017-01-23 06:00:00','~6'),('2017-01-23 06:15:00','~6'),('2017-01-23 06:30:00','~6'),('2017-01-23 06:45:00','~6'),('2017-01-23 07:00:00','~6'),('2017-01-23 07:30:00','~6'),('2017-01-23 07:45:00','~6'),('2017-01-23 08:00:00','~6'),('2017-01-23 08:15:00','~6'),('2017-01-22 10:00:00','~7'),('2017-01-24 10:00:00','~8'),('2017-01-09 07:15:00','~9'),('2017-01-23 07:15:00','~9');
/*!40000 ALTER TABLE `Calendar` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CalendarColors`
--

DROP TABLE IF EXISTS `CalendarColors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CalendarColors` (
  `Option` varchar(32) NOT NULL,
  `Value` varchar(64) NOT NULL,
  `ColorRed` float DEFAULT NULL,
  `ColorGreen` float DEFAULT NULL,
  `ColorBlue` float DEFAULT NULL,
  PRIMARY KEY (`Option`,`Value`),
  CONSTRAINT `CalendarColors_ibfk_1` FOREIGN KEY (`Option`) REFERENCES `PatientData` (`Option`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CalendarColors`
--

LOCK TABLES `CalendarColors` WRITE;
/*!40000 ALTER TABLE `CalendarColors` DISABLE KEYS */;
INSERT INTO `CalendarColors` VALUES ('First name','loldfsdf',0.8,0.6,0.8),('First name','Rebecca',0,0.2,0),('lol','lol',0.2,0.301961,0.701961);
/*!40000 ALTER TABLE `CalendarColors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PatientData`
--

DROP TABLE IF EXISTS `PatientData`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PatientData` (
  `SSN` char(9) NOT NULL,
  `Option` varchar(32) NOT NULL,
  `Value` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`SSN`,`Option`),
  KEY `fk_pd_option` (`Option`),
  CONSTRAINT `fk_pd_option` FOREIGN KEY (`Option`) REFERENCES `PatientDataOptions` (`Option`),
  CONSTRAINT `fk_pd_ssn` FOREIGN KEY (`SSN`) REFERENCES `Patients` (`SSN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PatientData`
--

LOCK TABLES `PatientData` WRITE;
/*!40000 ALTER TABLE `PatientData` DISABLE KEYS */;
INSERT INTO `PatientData` VALUES ('100000001','First Name','Rebecca'),('~4','First name','loldfsdf'),('~4','lol','lol'),('~4','new','');
/*!40000 ALTER TABLE `PatientData` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PatientDataOptions`
--

DROP TABLE IF EXISTS `PatientDataOptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PatientDataOptions` (
  `Option` varchar(32) NOT NULL,
  PRIMARY KEY (`Option`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PatientDataOptions`
--

LOCK TABLES `PatientDataOptions` WRITE;
/*!40000 ALTER TABLE `PatientDataOptions` DISABLE KEYS */;
INSERT INTO `PatientDataOptions` VALUES ('dsfj'),('First name'),('Last name'),('lol'),('new');
/*!40000 ALTER TABLE `PatientDataOptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Patients`
--

DROP TABLE IF EXISTS `Patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Patients` (
  `FirstName` varchar(64) DEFAULT NULL,
  `LastName` varchar(64) DEFAULT NULL,
  `SSN` char(9) NOT NULL,
  PRIMARY KEY (`SSN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Patients`
--

LOCK TABLES `Patients` WRITE;
/*!40000 ALTER TABLE `Patients` DISABLE KEYS */;
INSERT INTO `Patients` VALUES ('XXX','XXX','0'),('Jill','Valentine','000000000'),('Chris','Redfield','100000000'),('REbecca','Chambers','100000001'),('Albert','Wesker','~1'),('dlsf','lkj','~2'),('jhkjk','lkjlk','~3'),('sdf','sdsdfs','~4'),('dsf','dsf','~5'),('esdfe','dffw','~6'),('lol','lol','~7'),('k','k','~8'),('d','d','~9');
/*!40000 ALTER TABLE `Patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t1`
--

DROP TABLE IF EXISTS `t1`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t1` (
  `FirstName` varchar(64) NOT NULL,
  `LastName` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`FirstName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t1`
--

LOCK TABLES `t1` WRITE;
/*!40000 ALTER TABLE `t1` DISABLE KEYS */;
INSERT INTO `t1` VALUES ('lol','olo');
/*!40000 ALTER TABLE `t1` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-08-30  0:57:34
