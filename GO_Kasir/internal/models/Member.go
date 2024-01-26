package models

import "gorm.io/gorm"

type Member struct {
	gorm.Model
	CodeMember string `gorm:"type:varchar(20);not null" json:"code_member" form:"code_member"`
	Password   string `gorm:"type:varchar(100);not null" json:"password" form:"password"`
	Name       string `gorm:"type:varchar(50);not null" json:"name" form:"name"`
	Address    string `gorm:"type:varchar(100);not null" json:"address" form:"address"`
	Phone      string `gorm:"type:varchar(30);not null" json:"phone" form:"phone"`
	Point      uint   `json:"point" form:"point"`
	Transaksi  []Transaksi
}
