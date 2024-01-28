package TokoProfile

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
// @Tags Crud TokoProfile
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/tokoprofile [get]
func Index(c *fiber.Ctx) error {
	var cabang []models.TokoProfile

	if err := config.DB.Find(&cabang).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "data": cabang})
}

// Create godoc
// @Tags Crud TokoProfile
// @Accept json
// @Produce json
// @Param request body request.TokoProfileRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/tokoprofile [post]
func Create(c *fiber.Ctx) error {
	register := new(request.TokoProfileRequest)

	err := c.BodyParser(register)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var existingToko models.TokoProfile
	if err := config.DB.First(&existingToko, "name = ?", register.NameToko).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			karyawan := models.TokoProfile{
				Name:   register.NameToko,
				Npwp:   register.Npwp,
				Alamat: register.Alamat,
				Email:  register.Email,
				Telp:   register.Telp,
			}

			if err := config.DB.Create(&karyawan).Error; err != nil {
				return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
			}
			return c.Status(200).JSON(fiber.Map{"Message": "Berhasil created", "Data": karyawan})
		} else {
			return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
		}
	} else {
		// Record ditemukan, kembalikan error
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko sudah ada"})
	}
}

// Detail godoc
// @Tags Crud TokoProfile
// @Accept json
// @Produce json
// @Param id path int true "Toko ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/tokoprofile/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}

	var TokoProfile models.TokoProfile
	if err := config.DB.First(&TokoProfile, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Record Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Successfully created", "Data": TokoProfile})
}

// Update godoc
// @Tags Crud TokoProfile
// @Accept json
// @Produce json
// @Param id path int true "Toko ID"
// @Param request body request.TokoProfileRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/tokoprofile/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Format Id Salah"})
	}

	var existingTokoProfile models.TokoProfile
	if err := config.DB.First(&existingTokoProfile, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukkan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var requestTokoProfile request.TokoProfileRequest
	if err := c.BodyParser(&requestTokoProfile); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.Find(&existingTokoProfile, "name = ?", requestTokoProfile.NameToko).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {

			existingTokoProfile.Name = requestTokoProfile.NameToko
			existingTokoProfile.Npwp = requestTokoProfile.Npwp
			existingTokoProfile.Email = requestTokoProfile.Email
			existingTokoProfile.Telp = requestTokoProfile.Telp
			existingTokoProfile.Alamat = requestTokoProfile.Alamat

			if err := config.DB.Save(&existingTokoProfile).Error; err != nil {
				return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
			}

			return c.Status(fiber.StatusOK).JSON(fiber.Map{"Message": "Member data updated"})
		} else {
			return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
		}
	} else {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko sudah ada"})
	}

}

// Delete godoc
// @Tags Crud TokoProfile
// @Accept json
// @Produce json
// @Param id path int true "Toko ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/tokoprofile/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "error", "Message": "Format Id Salah"})
	}

	var tokoProfile models.TokoProfile
	if err := config.DB.First(&tokoProfile, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Id Petugas tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&tokoProfile, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "error", "Message": "Petugas tidak di temukan"})
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Data Petugas Deleted"})
}
