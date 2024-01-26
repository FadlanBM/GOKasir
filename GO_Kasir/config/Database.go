package config

import (
	"fmt"
	"github.com/ProjectLSP/internal/models"
	log "github.com/sirupsen/logrus"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

var DB *gorm.DB

func LoadDatabase() {
	conn := fmt.Sprintf("%v:%v@tcp(%v:%v)/%v?parseTime=true&loc=Asia%vJakarta", ENV.DB_USER, ENV.DB_PASSWORD, ENV.DB_HOST, ENV.DB_PORT, ENV.DB_DATABASE, "%2F")

	db, err := gorm.Open(mysql.Open(conn), &gorm.Config{})

	if err != nil {
		log.Fatal("Failed to open database", err)
	}

	db.AutoMigrate(&models.Barang{})
	db.AutoMigrate(&models.PembelianBarang{})
	db.AutoMigrate(&models.TokoProfile{})
	db.AutoMigrate(&models.Karyawan{})
	db.AutoMigrate(&models.Member{})
	db.AutoMigrate(&models.Transaksi{})
	db.AutoMigrate(&models.Voucer{})

	DB = db
	log.Println("Database loaded")

}
