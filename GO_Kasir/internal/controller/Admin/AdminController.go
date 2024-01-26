package Admin

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/helper"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"strconv"
)

// Index godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin [get]
func Index(c *fiber.Ctx) error {
	var karyawan []models.Karyawan

	if err := config.DB.Find(&karyawan, "role = ?", "admin").Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Internal Server Error", "Message": err.Error()})
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": karyawan})
}

// Create godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Param request body request.RegisterKaryawanRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin [post]
func Create(c *fiber.Ctx) error {
	register := new(request.RegisterKaryawanRequest)

	err := c.BodyParser(register)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	passwordHash, err := helper.HashPassword(register.Password)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var existingToko models.TokoProfile
	if err := config.DB.Find(&existingToko, "name= ?", register.NamaToko).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	karyawan := models.Karyawan{
		Nik:           register.Nik,
		Nama_Karyawan: register.Nama_Petugas,
		Username:      register.Username,
		Password:      passwordHash,
		Telp:          register.Telp,
		TokoProfileID: existingToko.ID,
		TokoProfile:   existingToko,
		Role:          "admin",
	}

	if err := config.DB.Create(&karyawan).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Berhasil Input data Karyawan", "Data": karyawan})
}

// Detail godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Param id path int true "Karyawan ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var karyawan models.Karyawan
	if err := config.DB.First(&karyawan, id, "role = ?", "admin").Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Detail", "Message": "Show Detail berhasil", "Data": karyawan})
}

// Update godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Param id path int true "Karyawan ID"
// @Param request body request.UpdateKaryawanRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Format param Id Salah"})
	}

	var updatedPetugas request.UpdateKaryawanRequest

	if err := c.BodyParser(&updatedPetugas); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingKaryawan models.Karyawan
	if err := config.DB.First(&existingKaryawan, id, "role = ?", "admin").Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Karyawan tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingToko models.TokoProfile
	if err := config.DB.Find(&existingToko, "name= ?", updatedPetugas.NamaToko).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	existingKaryawan.Username = updatedPetugas.Username
	existingKaryawan.Nama_Karyawan = updatedPetugas.Nama_Petugas
	existingKaryawan.Nik = updatedPetugas.Nik
	existingKaryawan.Telp = updatedPetugas.Telp
	existingKaryawan.TokoProfileID = existingToko.ID
	existingKaryawan.TokoProfile = existingToko

	if err := config.DB.Save(&existingKaryawan).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Status": "Success", "Message": "Petugas data updated"})
}

// UpdatePass godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Param id path int true "Karyawan ID"
// @Param request body request.UpdatePassRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin/resetPass/{id} [put]
func UpdatePass(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Format param Id Salah"})
	}

	var updatedPass request.UpdatePassRequest

	if err := c.BodyParser(&updatedPass); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingKaryawan models.Karyawan
	if err := config.DB.First(&existingKaryawan, id, "role = ?", "admin").Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Karyawan tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	if err := helper.VerivHash(existingKaryawan.Password, updatedPass.PasswordOld); err != nil {
		return c.Status(400).JSON(fiber.Map{"status": "Error", "message": "Password Salah"})
	}
	passwordHash, err := helper.HashPassword(updatedPass.PasswordNew)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	existingKaryawan.Password = passwordHash

	if err := config.DB.Save(&existingKaryawan).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Status": "Success", "Message": "Petugas data updated"})
}

// Delete godoc
// @Tags Crud Admin
// @Accept json
// @Produce json
// @Param id path int true "Karyawan ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/admin/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Invalid ID format"})
	}

	var karyawan models.Karyawan
	if err := config.DB.First(&karyawan, id, "role = ?", "admin").Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Id Petugas Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&karyawan, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Delete", "Message": err.Error()})
	}

	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Petugas Tidak di Temukan"})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Message": "Data Petugas Deleted"})
}
