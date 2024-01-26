package Member

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/helper"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"math/rand"
	"strconv"
	"time"
)

// Index godoc
// @Tags Crud Members
// @Accept json
// @Produce json
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/members [get]
func Index(c *fiber.Ctx) error {
	var member []models.Member

	if err := config.DB.Find(&member).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": member})
}

// Create godoc
// @Tags Crud Members
// @Accept json
// @Produce json
// @Param request body request.MemberRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/members [post]
func Create(c *fiber.Ctx) error {
	register := new(request.MemberRequest)

	err := c.BodyParser(register)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	length := 8
	rand.Seed(time.Now().UnixNano())

	characters := "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

	code := make([]byte, length)
	for i := 0; i < length; i++ {
		code[i] = characters[rand.Intn(len(characters))]
	}

	passwordHash, err := helper.HashPassword(register.Password)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	member := models.Member{
		CodeMember: string(code),
		Password:   passwordHash,
		Name:       register.Name,
		Address:    register.Address,
		Phone:      register.Phone,
	}

	if err := config.DB.Create(&member).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Successfully created", "Data": member})
}

// Detail godoc
// @Tags Crud Members
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/members/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var member models.Member
	if err := config.DB.First(&member, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record Tidak di Temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Successfully created", "Data": member})
}

// Update godoc
// @Tags Crud Members
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Param request body request.MemberRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/members/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Format Id Salah"})
	}

	var existingMember models.Member
	if err := config.DB.First(&existingMember, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID Member tidak di temukan"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var requestMember request.MemberRequest
	if err := c.BodyParser(&requestMember); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	passwordHash, err := helper.HashPassword(requestMember.Password)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	existingMember.Name = requestMember.Name
	existingMember.Password = passwordHash
	existingMember.Address = requestMember.Address
	existingMember.Phone = requestMember.Phone

	if err := config.DB.Save(&existingMember).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Message": "Member data updated"})
}

// Delete godoc
// @Tags Crud Members
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/members/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Id format salah"})
	}

	var member models.Member
	if err := config.DB.First(&member, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Id Petugas tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&member, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Petugas Not Found"})
	}
	return c.Status(200).JSON(fiber.Map{"Message": "Data Petugas Deleted"})
}
