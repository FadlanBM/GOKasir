package models

import (
	"gorm.io/gorm"
	"time"
)

type Voucer struct {
	gorm.Model
	Code      string    `gorm:"type:varchar(255);not null" json:"code" form:"code"`
	Discount  float64   `gorm:"type:decimal(15,2);not null" json:"discount" form:"discount"`
	StartDate time.Time `json:"start_date" form:"start_date"`
	EndDate   time.Time `json:"end_date" form:"end_date"`
	IsActive  string    `gorm:"type:enum('false','true')" json:"is_active" form:"is_active"`
}
