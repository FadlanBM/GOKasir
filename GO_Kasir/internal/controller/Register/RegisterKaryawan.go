package Register

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
)

// RegisterKaryawan godoc
// @Tags Crud Register
// @Accept json
// @Produce json
// @Param request body request.RegisterKaryawan true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/registerKaryawan [post]
func RegisterKaryawan(c *fiber.Ctx) error {
	reqregister := new(request.RegisterKaryawan)

	err := c.BodyParser(reqregister)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var existingToko models.Karyawan
	if err := config.DB.First(&existingToko, "nama_karyawan = ?", reqregister.NamaKaryawan).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.First(&existingToko, "nik = ?", reqregister.Nik).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err := config.DB.First(&existingToko, "username = ?", reqregister.Username).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Toko tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(200).JSON(fiber.Map{"status": "Insert", "message": "Akun terdaftar", "Data": existingToko})

}
