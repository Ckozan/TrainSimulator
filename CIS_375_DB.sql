-- MySQL Script generated by MySQL Workbench
-- Fri Apr 26 10:25:23 2019
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema CIS_375_SCHEMA
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema CIS_375_SCHEMA
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `CIS_375_SCHEMA` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE `CIS_375_SCHEMA` ;

-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Station`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Station` (
  `StationID` INT NOT NULL,
  `Freight` INT NOT NULL,
  `Random_On_Min` INT NOT NULL,
  `Random_On_Max` INT NOT NULL,
  `Random_Off_Min` INT NOT NULL,
  `Random_Off_Max` INT NOT NULL,
  `Ticket_Price` INT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`StationID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Station_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Station_Changes` (
  `StationID` INT NOT NULL,
  `Freight` INT NOT NULL,
  `Random_On_Min` INT NOT NULL,
  `Random_On_Max` INT NOT NULL,
  `Random_Off_Min` INT NOT NULL,
  `Random_Off_Max` INT NOT NULL,
  `Ticket_Price` INT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `StationID_idx` (`StationID` ASC),
  CONSTRAINT `StationID`
    FOREIGN KEY (`StationID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Hub`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Hub` (
  `HubID` INT NOT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`HubID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Hub_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Hub_Changes` (
  `HubID` INT NOT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `HubID_idx` (`HubID` ASC),
  CONSTRAINT `HubID`
    FOREIGN KEY (`HubID`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Track`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Track` (
  `TrackID` INT NOT NULL,
  `StartTime` INT NOT NULL,
  `EndTime` INT NOT NULL,
  `StartLocStationID` INT NULL,
  `StartLocHubID` INT NULL,
  `EndLocStationID` INT NULL,
  `EndLocHubID` INT NULL,
  `Length` INT NOT NULL,
  `SpeedLimit` INT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`TrackID`),
  INDEX `StartLocStationID_idx` (`StartLocStationID` ASC),
  INDEX `StartLocHubID_idx` (`StartLocHubID` ASC),
  INDEX `EndLocStationID_idx` (`EndLocStationID` ASC),
  INDEX `EndLocHubID_idx` (`EndLocHubID` ASC),
  CONSTRAINT `StartLocStationID`
    FOREIGN KEY (`StartLocStationID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StartLocHubID`
    FOREIGN KEY (`StartLocHubID`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocStationID`
    FOREIGN KEY (`EndLocStationID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocHubID`
    FOREIGN KEY (`EndLocHubID`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Track_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Track_Changes` (
  `TrackID` INT NOT NULL,
  `StartTime` INT NOT NULL,
  `EndTime` INT NOT NULL,
  `StartLocStationID2` INT NULL,
  `StartLocHubID2` INT NULL,
  `EndLocStationID2` INT NULL,
  `EndLocHubID2` INT NULL,
  `Length` INT NOT NULL,
  `SpeedLimit` INT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `StartLocStationID_idx` (`StartLocStationID2` ASC),
  INDEX `StartLocHubID_idx` (`StartLocHubID2` ASC),
  INDEX `EndLocStationID_idx` (`EndLocStationID2` ASC),
  INDEX `EndLocHubID_idx` (`EndLocHubID2` ASC),
  INDEX `TrackID_idx` (`TrackID` ASC),
  CONSTRAINT `StartLocStationID2`
    FOREIGN KEY (`StartLocStationID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StartLocHubID2`
    FOREIGN KEY (`StartLocHubID2`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocStationID2`
    FOREIGN KEY (`EndLocStationID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocHubID2`
    FOREIGN KEY (`EndLocHubID2`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID`
    FOREIGN KEY (`TrackID`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Train`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Train` (
  `TrainID` INT NOT NULL,
  `Freight` INT NOT NULL,
  `HomeHubID` INT NOT NULL,
  `Capacity` INT NULL,
  `TopSpeed` INT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`TrainID`),
  INDEX `HomeHubID_idx` (`HomeHubID` ASC),
  CONSTRAINT `HomeHubID`
    FOREIGN KEY (`HomeHubID`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Train_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Train_Changes` (
  `TrainID` INT NOT NULL,
  `Freight` INT NOT NULL,
  `HomeHubID2` INT NOT NULL,
  `Capacity` INT NULL,
  `TopSpeed` INT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `TrainID_idx` (`TrainID` ASC),
  INDEX `HomeHubID_idx` (`HomeHubID2` ASC),
  CONSTRAINT `TrainID`
    FOREIGN KEY (`TrainID`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `HomeHubID2`
    FOREIGN KEY (`HomeHubID2`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Train_Progress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Train_Progress` (
  `TrainID2` INT NOT NULL,
  `Freight` INT NOT NULL,
  `CurrentDay` INT NOT NULL,
  `CurrentMinute` INT NOT NULL,
  `CrewTime` INT NOT NULL,
  `TrackID2` INT NULL,
  `StationID2` INT NULL,
  `HubID2` INT NULL,
  `CrewChangeTime` INT NOT NULL,
  `DistanceTraveled` DECIMAL(7,2) NOT NULL,
  `CurrentSpeed` DECIMAL(5,2) NOT NULL,
  INDEX `TrainID_idx` (`TrainID2` ASC),
  INDEX `TrackID_idx` (`TrackID2` ASC),
  INDEX `StationID_idx` (`StationID2` ASC),
  INDEX `HubID_idx` (`HubID2` ASC),
  CONSTRAINT `TrainID2`
    FOREIGN KEY (`TrainID2`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID2`
    FOREIGN KEY (`TrackID2`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StationID2`
    FOREIGN KEY (`StationID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `HubID2`
    FOREIGN KEY (`HubID2`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Freight_Route`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Freight_Route` (
  `RouteID` INT NOT NULL,
  `StartLocID` INT NOT NULL,
  `EndLocID` INT NOT NULL,
  `StartTime` INT NULL,
  `Repeating` INT NOT NULL,
  `RunDay` INT NOT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`RouteID`),
  INDEX `StartLocID_idx` (`StartLocID` ASC),
  INDEX `EndLocID_idx` (`EndLocID` ASC),
  CONSTRAINT `StartLocID`
    FOREIGN KEY (`StartLocID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocID`
    FOREIGN KEY (`EndLocID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Freight_Route_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Freight_Route_Changes` (
  `RouteID` INT NOT NULL,
  `StartLocID2` INT NOT NULL,
  `EndLocID2` INT NOT NULL,
  `StartTime` INT NULL,
  `Repeating` INT NOT NULL,
  `RunDay` INT NOT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `RouteID_idx` (`RouteID` ASC),
  INDEX `StartLocID_idx` (`StartLocID2` ASC),
  INDEX `EndLocID_idx` (`EndLocID2` ASC),
  CONSTRAINT `RouteID`
    FOREIGN KEY (`RouteID`)
    REFERENCES `CIS_375_SCHEMA`.`Freight_Route` (`RouteID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StartLocID2`
    FOREIGN KEY (`StartLocID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `EndLocID2`
    FOREIGN KEY (`EndLocID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Freight_Route_Progress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Freight_Route_Progress` (
  `RouteID2` INT NOT NULL,
  `TrainID3` INT NOT NULL,
  `AssignedTime` INT NOT NULL,
  `ArrivalTime` INT NULL,
  `RerouteCounter` INT NOT NULL,
  `TotalTime` INT NOT NULL,
  `RunDay` INT NOT NULL,
  INDEX `RouteID_idx` (`RouteID2` ASC),
  INDEX `TrainID_idx` (`TrainID3` ASC),
  CONSTRAINT `RouteID2`
    FOREIGN KEY (`RouteID2`)
    REFERENCES `CIS_375_SCHEMA`.`Freight_Route` (`RouteID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrainID3`
    FOREIGN KEY (`TrainID3`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Passenger_Route`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Passenger_Route` (
  `RouteID` INT NOT NULL,
  `StopNumber` INT NOT NULL,
  `LocID` INT NOT NULL,
  `StartTime` INT NOT NULL,
  `Repeating` INT NOT NULL,
  `RunDay` INT NOT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`RouteID`, `StopNumber`),
  INDEX `LocID_idx` (`LocID` ASC),
  CONSTRAINT `LocID`
    FOREIGN KEY (`LocID`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Passenger_Route_Changes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Passenger_Route_Changes` (
  `RouteID3` INT NOT NULL,
  `StopNumber` INT NOT NULL,
  `LocID2` INT NOT NULL,
  `StartTime` INT NOT NULL,
  `Repeating` INT NOT NULL,
  `RunDay` INT NOT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `RouteID_idx` (`RouteID3` ASC, `StopNumber` ASC),
  INDEX `LocID_idx` (`LocID2` ASC),
  CONSTRAINT `RouteID3`
    FOREIGN KEY (`RouteID3` , `StopNumber`)
    REFERENCES `CIS_375_SCHEMA`.`Passenger_Route` (`RouteID` , `StopNumber`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `LocID2`
    FOREIGN KEY (`LocID2`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Passenger_Route_Progress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Passenger_Route_Progress` (
  `RouteID4` INT NOT NULL,
  `StopNumber2` INT NOT NULL,
  `LocID3` INT NOT NULL,
  `TrainID4` INT NOT NULL,
  `AssignedTime` INT NOT NULL,
  `ArrivalTime` INT NULL,
  `RerouteCounter` INT NOT NULL,
  `TotalTime` INT NOT NULL,
  `RunDay` INT NOT NULL,
  INDEX `RouteID_idx` (`RouteID4` ASC, `StopNumber2` ASC),
  INDEX `LocID_idx` (`LocID3` ASC),
  INDEX `TrainID_idx` (`TrainID4` ASC),
  CONSTRAINT `RouteID4`
    FOREIGN KEY (`RouteID4` , `StopNumber2`)
    REFERENCES `CIS_375_SCHEMA`.`Passenger_Route` (`RouteID` , `StopNumber`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `LocID3`
    FOREIGN KEY (`LocID3`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrainID4`
    FOREIGN KEY (`TrainID4`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Maintenance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Maintenance` (
  `MaintenanceID` INT NOT NULL,
  `StationID3` INT NULL,
  `TrackID3` INT NULL,
  `TrainID5` INT NULL,
  `StartDay` INT NOT NULL,
  `EndDay` INT NOT NULL,
  `Active` INT NOT NULL,
  PRIMARY KEY (`MaintenanceID`),
  INDEX `StationID_idx` (`StationID3` ASC),
  INDEX `TrackID_idx` (`TrackID3` ASC),
  INDEX `TrainID_idx` (`TrainID5` ASC),
  CONSTRAINT `StationID3`
    FOREIGN KEY (`StationID3`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID3`
    FOREIGN KEY (`TrackID3`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrainID5`
    FOREIGN KEY (`TrainID5`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Maintenance_Updates`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Maintenance_Updates` (
  `MaintenanceID` INT NOT NULL,
  `StationID4` INT NULL,
  `TrackID4` INT NULL,
  `TrainID6` INT NULL,
  `StartDay` INT NOT NULL,
  `EndDay` INT NOT NULL,
  `Active` INT NOT NULL,
  `Change_Date` INT NOT NULL,
  INDEX `MaintenanceID_idx` (`MaintenanceID` ASC),
  INDEX `StationID_idx` (`StationID4` ASC),
  INDEX `TrackID_idx` (`TrackID4` ASC),
  INDEX `TrainID6_idx` (`TrainID6` ASC),
  CONSTRAINT `MaintenanceID`
    FOREIGN KEY (`MaintenanceID`)
    REFERENCES `CIS_375_SCHEMA`.`Maintenance` (`MaintenanceID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StationID4`
    FOREIGN KEY (`StationID4`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID4`
    FOREIGN KEY (`TrackID4`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrainID6`
    FOREIGN KEY (`TrainID6`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Weather`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Weather` (
  `WeatherID` INT NOT NULL,
  `WeatherType` VARCHAR(45) NOT NULL,
  `Impact` DECIMAL(4,3) NOT NULL,
  PRIMARY KEY (`WeatherID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Weather_History`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Weather_History` (
  `WeatherID` INT NOT NULL,
  `TrackID5` INT NOT NULL,
  `Day` INT NOT NULL,
  `MinuteStarted` INT NOT NULL,
  `MinuteEnded` INT NULL,
  INDEX `WeatherID_idx` (`WeatherID` ASC),
  INDEX `TrackID_idx` (`TrackID5` ASC),
  CONSTRAINT `WeatherID`
    FOREIGN KEY (`WeatherID`)
    REFERENCES `CIS_375_SCHEMA`.`Weather` (`WeatherID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID5`
    FOREIGN KEY (`TrackID5`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Day`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Day` (
  `DayID` INT NOT NULL,
  `DayType` INT NOT NULL,
  `Holiday` INT NOT NULL,
  PRIMARY KEY (`DayID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Dijkstra_Baseline`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Dijkstra_Baseline` (
  `StartingVertexID_Station` INT NULL,
  `StartingVertexID_Hub` INT NULL,
  `VertexID_Station` INT NULL,
  `VertexID_Hub` INT NULL,
  `ShortestTime` INT NULL,
  `PreviousVertexID_Station` INT NULL,
  `PreviousVertexID_Hub` INT NULL,
  `Baseline_Type` INT NOT NULL,
  `RunDay` INT NOT NULL,
  INDEX `StartingVertexID_idx` (`StartingVertexID_Station` ASC),
  INDEX `VertexID_idx` (`VertexID_Station` ASC),
  INDEX `PreviousVertexID_idx` (`PreviousVertexID_Station` ASC),
  INDEX `StartingVertexID_Hub_idx` (`StartingVertexID_Hub` ASC),
  INDEX `VertexID_Hub_idx` (`VertexID_Hub` ASC),
  INDEX `PreviousVertexID_Hub_idx` (`PreviousVertexID_Hub` ASC),
  CONSTRAINT `StartingVertexID_Station`
    FOREIGN KEY (`StartingVertexID_Station`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `VertexID_Station`
    FOREIGN KEY (`VertexID_Station`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `PreviousVertexID_Station`
    FOREIGN KEY (`PreviousVertexID_Station`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StartingVertexID_Hub`
    FOREIGN KEY (`StartingVertexID_Hub`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `VertexID_Hub`
    FOREIGN KEY (`VertexID_Hub`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `PreviousVertexID_Hub`
    FOREIGN KEY (`PreviousVertexID_Hub`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Finances`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Finances` (
  `ItemModified` VARCHAR(45) NOT NULL,
  `TypeOfModification` VARCHAR(45) NOT NULL,
  `CostOfModification` INT NOT NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Ticket_Income`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Ticket_Income` (
  `StationID6` INT NOT NULL,
  `TrainID8` INT NOT NULL,
  `Minute` INT NOT NULL,
  `Day` INT NOT NULL,
  `PassengerCount` INT NOT NULL,
  `TicketCost` INT NOT NULL,
  INDEX `StationID_idx` (`StationID6` ASC),
  INDEX `TrainID_idx` (`TrainID8` ASC),
  CONSTRAINT `StationID6`
    FOREIGN KEY (`StationID6`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrainID8`
    FOREIGN KEY (`TrainID8`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Breakpoints`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Breakpoints` (
  `Description` TEXT(65535) NOT NULL,
  `Minunte` INT NOT NULL,
  `Day` INT NOT NULL,
  `Field1` VARCHAR(45) NULL,
  `Field2` VARCHAR(45) NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Error_Description`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Error_Description` (
  `ErrorID` INT NOT NULL,
  `Description` TEXT(65535) NOT NULL,
  PRIMARY KEY (`ErrorID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Error_Log`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Error_Log` (
  `ErrorID` INT NOT NULL,
  `Minute` INT NOT NULL,
  `Day` INT NOT NULL,
  INDEX `ErrorID_idx` (`ErrorID` ASC),
  CONSTRAINT `ErrorID`
    FOREIGN KEY (`ErrorID`)
    REFERENCES `CIS_375_SCHEMA`.`Error_Description` (`ErrorID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`File_Number`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`File_Number` (
  `Number` INT NOT NULL)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Finance_History`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Finance_History` (
  `Day` INT NOT NULL,
  `ItemModified` VARCHAR(45) NOT NULL,
  `TypeOfModification` VARCHAR(45) NOT NULL,
  `CostOfModification` BIGINT NOT NULL,
  `LengthOfTrack` INT NULL,
  `CapacityChange` INT NULL,
  `TrainID7` INT NULL,
  `HubID3` INT NULL,
  `StationID5` INT NULL,
  `TrackID6` INT NULL,
  INDEX `TrainID7_idx` (`TrainID7` ASC),
  INDEX `HubID3_idx` (`HubID3` ASC),
  INDEX `StationID5_idx` (`StationID5` ASC),
  INDEX `TrackID6_idx` (`TrackID6` ASC),
  CONSTRAINT `TrainID7`
    FOREIGN KEY (`TrainID7`)
    REFERENCES `CIS_375_SCHEMA`.`Train` (`TrainID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `HubID3`
    FOREIGN KEY (`HubID3`)
    REFERENCES `CIS_375_SCHEMA`.`Hub` (`HubID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `StationID5`
    FOREIGN KEY (`StationID5`)
    REFERENCES `CIS_375_SCHEMA`.`Station` (`StationID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TrackID6`
    FOREIGN KEY (`TrackID6`)
    REFERENCES `CIS_375_SCHEMA`.`Track` (`TrackID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `CIS_375_SCHEMA`.`Config`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `CIS_375_SCHEMA`.`Config` (
  `CValue` INT NOT NULL,
  `CrewWaitTime` INT NOT NULL)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
