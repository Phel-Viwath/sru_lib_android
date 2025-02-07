/*
 * Copyright (c) 2025.
 * @Author Phel Viwath
 * All rights reserved.
 *
 */

package com.viwath.srulibrarymobile.domain.model

// student
typealias StudentId = Long
typealias StudentName = String
typealias Gender = String
typealias DateOfBirth = String
typealias DegreeLevel = String
typealias MajorName = String
typealias Generation = Int

// attend
typealias AttendId = Long
typealias EntryTimes = String
typealias ExitingTimes = String
typealias Purpose = String
typealias Date = String
typealias Major = String
typealias Status = String

// book
typealias BookId = String
typealias BookTitle = String
typealias BookQuantity = Int
typealias Author = String
typealias PublicationYear = Int
typealias Genre = String
typealias ReceiveDate = String
typealias IsActive = Boolean
typealias InActiveDate = String

//language
typealias LanguageId = String
typealias LanguageName = String

//college
typealias CollegeId = String
typealias CollegeName = String

// Auth
typealias RefreshToken = String
typealias AccessToken = String
typealias Username = String
typealias Email = String
typealias Password = String
typealias Role = String
typealias Message = String

// Borrow (borrow model)
typealias BorrowId = Long
typealias BorrowDate = String
typealias GiveBackDate = String
typealias IsBringBack = Boolean

// Donation
typealias DonateDate = String
typealias DonatorId = Int
typealias DonatorName = String
