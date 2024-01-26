package PembelianBarang

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"strconv"
)

// Index godoc
// @Tags Crud PembelianBarang
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/pembelianbarang [get]
func Index(c *fiber.Ctx) error {
	var pembelianBarang []models.PembelianBarang

	if err := config.DB.Find(&pembelianBarang).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": pembelianBarang})
}

// Create godoc
// @Tags Crud PembelianBarang
// @Accept json
// @Produce json
// @Param request body request.PembelianBarangRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/pembelianbarang [post]
func Create(c *fiber.Ctx) error {
	req := new(request.PembelianBarangRequest)

	err := c.BodyParser(req)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingBarang models.Barang
	if err := config.DB.First(&existingBarang, req.BarangID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Barang tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingTransaksi models.Transaksi
	if err := config.DB.First(&existingTransaksi, req.TransaksiID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Data Transaksi tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	karyawan := models.PembelianBarang{
		TransaksiID:   req.TransaksiID,
		BarangID:      req.BarangID,
		Quantity:      req.Quantity,
		SubTotalHarga: req.SubTotalHarga,
		Discount:      req.Discount,
	}

	if err := config.DB.Create(&karyawan).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": karyawan})
}

// Detail godoc
// @Tags Crud PembelianBarang
// @Accept json
// @Produce json
// @Param id path int true "PembelianBarang ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/pembelianbarang/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var pembelianBarang models.PembelianBarang
	if err := config.DB.First(&pembelianBarang, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": pembelianBarang})
}

// Update godoc
// @Tags Crud PembelianBarang
// @Accept json
// @Produce json
// @Param id path int true "PembelianBarang ID"
// @Param request body request.PembelianBarangRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/pembelianbarang/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Format ID Salah"})
	}

	var req request.PembelianBarangRequest
	if err := c.BodyParser(&req); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingBarangTransaksi models.PembelianBarang
	if err := config.DB.First(&existingBarangTransaksi, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Tidak di Temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingTransaksi models.Transaksi
	if err := config.DB.First(&existingTransaksi, req.TransaksiID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Transaksi Tidak di Temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingBarang models.Barang
	if err := config.DB.First(&existingBarang, req.BarangID).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Barang Tidak di Temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	existingBarangTransaksi.TransaksiID = existingTransaksi.ID
	existingBarangTransaksi.BarangID = existingBarang.ID
	existingBarangTransaksi.Quantity = req.Quantity
	existingBarangTransaksi.SubTotalHarga = req.SubTotalHarga
	existingBarangTransaksi.Discount = req.Discount

	if err := config.DB.Save(&existingBarangTransaksi).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Status": "Success", "Message": "Member data updated"})
}

// Delete godoc
// @Tags Crud PembelianBarang
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/pembelianbarang/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Invalid ID format"})
	}

	var pembelianBarang models.PembelianBarang
	if err := config.DB.First(&pembelianBarang, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Id Petugas tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&pembelianBarang, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Petugas Not Found"})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Message": "Data Petugas Deleted"})
}
